package com.diploma.aerodent.ui.user;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.diploma.aerodent.data.local.entity.User;
import com.diploma.aerodent.data.local.model.UserRole;
import com.diploma.aerodent.data.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application mockApplication;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private SharedPreferences mockPrefs;
    @Mock
    private SharedPreferences.Editor mockEditor;

    private UserViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockApplication.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        when(mockPrefs.edit()).thenReturn(mockEditor);

        when(mockUserRepository.getAllUsers()).thenReturn(new MutableLiveData<>());
        when(mockUserRepository.getActiveUsers()).thenReturn(new MutableLiveData<>());

        viewModel = new UserViewModel(mockApplication, mockUserRepository);
    }

    @Test
    public void testCanManageUsersByRole() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);
        assertTrue(viewModel.canManageUsers(admin));

        User dentist = new User();
        dentist.setRole(UserRole.DENTIST);
        assertTrue(viewModel.canManageUsers(dentist));

        User assistant = new User();
        assistant.setRole(UserRole.ASSISTANT);
        assertFalse(viewModel.canManageUsers(assistant));
    }

    @Test
    public void testCanViewAdvancedSettingsByRole() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);
        assertTrue(viewModel.canViewAdvancedSettings(admin));

        User dentist = new User();
        dentist.setRole(UserRole.DENTIST);
        assertTrue(viewModel.canViewAdvancedSettings(dentist));

        User assistant = new User();
        assistant.setRole(UserRole.ASSISTANT);
        assertFalse(viewModel.canViewAdvancedSettings(assistant));
    }

    @Test
    public void testCanEditSelfRole() {
        when(mockPrefs.getString("loggedInUserId", null)).thenReturn("550e8400-e29b-41d4-a716-446655440000");

        boolean canEditSelf = viewModel.canEditRole(true, "550e8400-e29b-41d4-a716-446655440000");

        boolean canEditOther = viewModel.canEditRole(true, "550e8400-e29b-41d4-a716-446655440001");

        assertFalse(canEditSelf);
        assertTrue(canEditOther);
    }

    @Test
    public void testCanEditPinRestrictions() {
        when(mockPrefs.getString("loggedInUserId", null)).thenReturn("550e8400-e29b-41d4-a716-446655440002");
        when(mockPrefs.getString("loggedInUserRole", null)).thenReturn("DENTIST");

        boolean canEditOwnPin = viewModel.canEditPin(true, "550e8400-e29b-41d4-a716-446655440002");
        assertTrue(canEditOwnPin);

        boolean canEditOtherPin = viewModel.canEditPin(true, "550e8400-e29b-41d4-a716-446655440003");
        assertFalse(canEditOtherPin);
    }

    @Test
    public void testIsDentist() {
        when(mockPrefs.getString("loggedInUserRole", null)).thenReturn("DENTIST");
        assertTrue(viewModel.isDentist());

        when(mockPrefs.getString("loggedInUserRole", null)).thenReturn("ADMIN");
        assertFalse(viewModel.isDentist());
    }
}
