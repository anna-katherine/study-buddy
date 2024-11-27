package com.example.studybuddy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;

import android.content.Context;

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

        // Prepare mocks
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        // Create a partial mock of DashboardActivity
        dashboardActivity = mock(DashboardActivity.class, CALLS_REAL_METHODS);
    }

//    @Test
//    public void testCreateGroup_EmptyGroupName() {
//        // Setup
//        doCallRealMethod().when(dashboardActivity).createGroup(anyString(), anyString());
//
//        // Prepare a mock context for Toast
//        dashboardActivity.createGroup("", "TestCourse");
//
//        // Verify behavior
//        verify(dashboardActivity).createGroup("", "TestCourse");
//    }

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

//    @Test
//    public void testRemoveGroup() {
//        String groupName = "TestGroup";
//
//        when(mockFirestore.collection("groups")).thenReturn(mockGroupsCollection);
//        when(mockGroupsCollection.document(groupName)).thenReturn(mockGroupDocument);
//        when(mockFirestore.collection("users").document(anyString())).thenReturn(mockUserDocument);
//
//        doCallRealMethod().when(dashboardActivity).removeGroup(groupName);
//
//        dashboardActivity.removeGroup(groupName);
//
//        verify(dashboardActivity).removeGroup(groupName);
//    }

//    @Test
//    public void testInitializeGroupList() {
//        // Mock FirebaseAuth and FirebaseUser
//        FirebaseAuth mockAuth = mock(FirebaseAuth.class);
//        FirebaseUser mockUser = mock(FirebaseUser.class);
//        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
//
//        // Mock Firestore response objects
//        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
//        Task<QuerySnapshot> mockQuerySnapshotTask = mock(Task.class);
//        when(mockQuerySnapshotTask.isSuccessful()).thenReturn(true);
//        when(mockQuerySnapshotTask.getResult()).thenReturn(mockQuerySnapshot);
//
//        CollectionReference mockCollectionReference = mock(CollectionReference.class);
//        when(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference);
//        when(mockCollectionReference.get()).thenReturn(mockQuerySnapshotTask);
//
//
//        dashboardActivity = new DashboardActivity();
//
//        dashboardActivity.auth = mockAuth;
//
//        // Call the method you want to test
//        doCallRealMethod().when(dashboardActivity).initializeGroupListAndShowDialog();
//        dashboardActivity.initializeGroupListAndShowDialog();  // Now the object is properly instantiated
//
//        // Verify that the method was called
//        verify(dashboardActivity).initializeGroupListAndShowDialog();
//    }



//    @Test
//    public void testFetchUserData() {
//        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
//        when(mockQuerySnapshotTask.isSuccessful()).thenReturn(true);
//        when(mockQuerySnapshotTask.getResult()).thenReturn(mockQuerySnapshot);
//
//        doCallRealMethod().when(dashboardActivity).fetchUserData(anyString());
//        dashboardActivity.fetchUserData("SeB5ppnaPoaBY6IBQPnAbhmtF283");
//        verify(dashboardActivity).fetchUserData("SeB5ppnaPoaBY6IBQPnAbhmtF283");
//    }
}