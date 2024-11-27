package com.example.studybuddy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Transaction;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardActivityTest {
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockUser;
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private FirebaseFirestore db;
    @Mock
    private CollectionReference mockGroupsCollection;
    @Mock
    private DocumentReference mockUserDocument;
    @Mock
    private DocumentReference mockGroupDocument;
    @Mock
    private Task<DocumentSnapshot> mockDocumentSnapshotTask;
    @Mock
    private Task<QuerySnapshot> mockQuerySnapshotTask;
    @Mock
    private Context mockContext;
    private DashboardActivity dashboardActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        when(mockFirestore.collection("users")).thenReturn(mockGroupsCollection);
        when(mockGroupsCollection.document(anyString())).thenReturn(mockUserDocument);
        when(mockGroupsCollection.document(anyString()).collection("groups")).thenReturn(mockGroupsCollection);

        dashboardActivity = mock(DashboardActivity.class, CALLS_REAL_METHODS);
        dashboardActivity.auth = mockAuth;
    }

    @Test
    public void testCreateGroup_EmptyGroupName() {

        dashboardActivity = mock(DashboardActivity.class);
        doCallRealMethod().when(dashboardActivity).createGroup(anyString(), anyString());

        doNothing().when(dashboardActivity).showToast(anyString());

        String result = dashboardActivity.createGroup("", "TestCourse");

        assertEquals("Group name cannot be empty", result);
        verify(dashboardActivity).showToast("No group name provided");
        verify(dashboardActivity).createGroup("", "TestCourse");
    }

    @Test
    public void testHandleGroupSelection() {
        List<String> selectedGroups = Arrays.asList("Group1", "Group2");
        String userID = "testUserId";

        when(mockFirestore.collection("users")).thenReturn(mockGroupsCollection);
        when(mockGroupsCollection.document(userID)).thenReturn(mockUserDocument);

        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        when(mockDocumentSnapshotTask.isSuccessful()).thenReturn(true);
        when(mockDocumentSnapshotTask.getResult()).thenReturn(mockDocumentSnapshot);

        List<String> existingGroups = new ArrayList<>();
        when(mockDocumentSnapshot.get("groupList")).thenReturn(existingGroups);

        doCallRealMethod().when(dashboardActivity).handleGroupSelection(selectedGroups, userID);

        dashboardActivity.handleGroupSelection(selectedGroups, userID);

        verify(dashboardActivity).handleGroupSelection(selectedGroups, userID);
    }



    @Test
    public void testInitializeGroupList() {
        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        FirebaseFirestore mockFirestore = mock(FirebaseFirestore.class);
        CollectionReference mockCollectionReference = mock(CollectionReference.class);
        Task<QuerySnapshot> mockQuerySnapshotTask = mock(Task.class);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);

        when(mockQuerySnapshotTask.isSuccessful()).thenReturn(true);
        when(mockQuerySnapshotTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockFirestore.collection("groups")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.get()).thenReturn(mockQuerySnapshotTask);

        dashboardActivity.auth = mockAuth;
        dashboardActivity.db = mockFirestore;

        doCallRealMethod().when(dashboardActivity).initializeGroupListAndShowDialog();

        dashboardActivity.initializeGroupListAndShowDialog();

        verify(dashboardActivity).initializeGroupListAndShowDialog();
        verify(mockFirestore).collection("groups");
        verify(mockCollectionReference).get();
    }



}