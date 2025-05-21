package com.unibuc.mds.memoreasy.Controllers;

import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HomePageControllerIT extends ApplicationTest {

    private static final String testUsername = "testuser_home";
    private static final String testPassword = "testpass_home";
    private int testUserId;

    private HomePageController controller;
    private Label labelWelcome;
    private Label dailyStreakLabel;
    private LineChart<String, Number> performanceChart;

    @BeforeAll
    void setupTestUserAndAuditData() throws Exception {
        String hashed = BCrypt.hashpw(testPassword, BCrypt.gensalt());
        try (Connection con = DatabaseUtils.getConnection()) {
            PreparedStatement deleteAudit = con.prepareStatement(
                    "DELETE FROM audit WHERE id_user = (SELECT id_user FROM user WHERE name = ?)"
            );
            deleteAudit.setString(1, testUsername);
            deleteAudit.executeUpdate();

            PreparedStatement deleteUser = con.prepareStatement(
                    "DELETE FROM user WHERE name = ?"
            );
            deleteUser.setString(1, testUsername);
            deleteUser.executeUpdate();

            PreparedStatement insertUser = con.prepareStatement(
                    "INSERT INTO user(name, password) VALUES (?, ?)"
            );
            insertUser.setString(1, testUsername);
            insertUser.setString(2, hashed);
            insertUser.executeUpdate();

            PreparedStatement getUserId = con.prepareStatement(
                    "SELECT id_user FROM user WHERE name = ?"
            );
            getUserId.setString(1, testUsername);
            ResultSet rs = getUserId.executeQuery();
            if (rs.next()) {
                testUserId = rs.getInt(1);
            }

            // Insert two weeks of audit data BEFORE UI loads
            PreparedStatement insertAudit = con.prepareStatement(
                    "INSERT INTO audit(id_user, activity_date, no_flashcards) VALUES (?, ?, ?)"
            );
            insertAudit.setInt(1, testUserId);
            insertAudit.setDate(2, Date.valueOf(LocalDate.now().minusWeeks(1)));
            insertAudit.setInt(3, 5);
            insertAudit.executeUpdate();

            insertAudit.setInt(1, testUserId);
            insertAudit.setDate(2, Date.valueOf(LocalDate.now()));
            insertAudit.setInt(3, 10);
            insertAudit.executeUpdate();
        }
        DatabaseUtils.authenticateUser(testUsername, testPassword);
    }

    @AfterAll
    void cleanupTestUserAndAuditData() throws Exception {
        try (Connection con = DatabaseUtils.getConnection()) {
            PreparedStatement deleteAudit = con.prepareStatement(
                    "DELETE FROM audit WHERE id_user = ?"
            );
            deleteAudit.setInt(1, testUserId);
            deleteAudit.executeUpdate();

            PreparedStatement deleteUser = con.prepareStatement(
                    "DELETE FROM user WHERE id_user = ?"
            );
            deleteUser.setInt(1, testUserId);
            deleteUser.executeUpdate();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unibuc/mds/memoreasy/Views/HomePage/HomePageView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.addName(testUsername);
        stage.setScene(new Scene(root));
        stage.show();

        // Wait for UI to load and refresh
        WaitForAsyncUtils.waitForFxEvents();

        labelWelcome = lookup("#labelWelcome").query();
        dailyStreakLabel = lookup("#dailyStreakLabel").query();
        performanceChart = lookup("#performanceChart").query();
    }

    @Test
    void testWelcomeLabelAndStreak() {
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(labelWelcome, "labelWelcome should not be null");
        assertNotNull(dailyStreakLabel, "dailyStreakLabel should not be null");
        assertTrue(labelWelcome.getText().contains(testUsername),
                "Welcome label should contain the username: " + labelWelcome.getText());
        assertTrue(dailyStreakLabel.getText().toLowerCase().contains("streak"),
                "Daily streak label should mention streak: " + dailyStreakLabel.getText());
    }

    @Test
    void testPerformanceChartData() {
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(performanceChart, "performanceChart should not be null");
        assertFalse(performanceChart.getData().isEmpty(), "Performance chart data should not be empty");
        XYChart.Series<String, Number> series = performanceChart.getData().get(0);
        assertNotNull(series.getName(), "Series name should not be null");
        assertTrue(series.getData().size() >= 2, "Should have at least 2 data points");
        boolean found5 = false, found10 = false;
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getYValue().intValue() == 5) found5 = true;
            if (data.getYValue().intValue() == 10) found10 = true;
        }
        assertTrue(found5, "Should find a data point with value 5");
        assertTrue(found10, "Should find a data point with value 10");
    }
}