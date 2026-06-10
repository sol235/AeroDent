package com.diploma.aerodent.ui.user;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.diploma.aerodent.data.local.entity.User;
import com.diploma.aerodent.data.local.model.UserRole;
import com.diploma.aerodent.data.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthViewModelTest {

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

    private AuthViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockApplication.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        when(mockPrefs.edit()).thenReturn(mockEditor);

        when(mockApplication.getString(anyInt())).thenReturn("Error");

        viewModel = new AuthViewModel(mockApplication, mockUserRepository);
    }

    @Test
    public void testVerifyPinWithCorrectPin() {
        User user = new User();
        user.setId("550e8400-e29b-41d4-a716-446655440000");
        user.setPin("5731");
        user.setRole(UserRole.DENTIST);
        user.setFullName("Yoan Petkov");

        viewModel.verifyPin(user, "5731");

        assertTrue(viewModel.getLoginSuccess().getValue());

        // Verify SessionManager saved the user session
        verify(mockEditor).putString("loggedInUserId", "550e8400-e29b-41d4-a716-446655440000");
        verify(mockEditor).putString("loggedInUserRole", "DENTIST");
        verify(mockEditor).putString("loggedInUserName", "Yoan Petkov");
    }

    @Test
    public void testVerifyPinWithIncorrectPin() {
        User user = new User();
        user.setId("550e8400-e29b-41d4-a716-446655440000");
        user.setPin("4136");
        user.setRole(UserRole.DENTIST);

        viewModel.verifyPin(user, "0000");

        // Verify loginSuccess is false
        assertFalse(viewModel.getLoginSuccess().getValue());
        assertEquals("Error", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCreateInitialAdminWithEmptyFields() {
        viewModel.createInitialAdmin("", "", "5731", "1800123456", null, "1205341203");

        // Verify validation fails on empty fields
        assertEquals("Error", viewModel.getErrorMessage().getValue());
        assertNull(viewModel.getActionComplete().getValue());
    }

    @Test
    public void testCreateInitialAdminWithPinMismatch() {
        viewModel.createInitialAdmin("Dimitar Nikolov", "4826", "5678", "1800123456", null, "1205341203");

        // Verify validation fails on mismatched pins
        assertEquals("Error", viewModel.getErrorMessage().getValue());
        assertNull(viewModel.getActionComplete().getValue());
    }

    @Test
    public void testCreateInitialAdminWithShortPin() {
        viewModel.createInitialAdmin("", "12", "12", "1800123456", null, "1205341203");

        // Verify validation fails on short pin
        assertEquals("Error", viewModel.getErrorMessage().getValue());
        assertNull(viewModel.getActionComplete().getValue());
    }

    @Test
    public void testCreateInitialAdminWithEmptyRzi() {
        viewModel.createInitialAdmin("Georgi Rakovski", "4826", "4826", "1800123456", null, "");

        // Verify validation fails on empty RZI
        assertEquals("Error", viewModel.getErrorMessage().getValue());
        assertNull(viewModel.getActionComplete().getValue());
    }
}
