package com.unibuc.mds.memoreasy.Controllers;
import com.unibuc.mds.memoreasy.Utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    @FXML
    private Label labelWelcome;

    @FXML
    private Label dailyStreakLabel;

    void addName(String name){
        labelWelcome.setText("Welcome, "+name+"!");
    }

    private void setDailyStreakLabel(int streak){
        String number;
        if(streak==-1){
            number="x";
        }
        else{
            number=Integer.toString(streak);
        }
        dailyStreakLabel.setText("Daily Streak: " + number);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDailyStreakLabel(DatabaseUtils.getCurrentStreak());
        loadWeeklyPerformance();
    }
    @FXML
    private LineChart<String, Number> performanceChart;

    private void loadWeeklyPerformance() {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
            SELECT MIN(activity_date) AS start_date
            FROM audit
            WHERE id_user = ?;
        """;

            PreparedStatement startDateStatement = connection.prepareStatement(query);
            int userId = DatabaseUtils.getIdUser();
            startDateStatement.setInt(1, userId);

            ResultSet startDateResult = startDateStatement.executeQuery();
            LocalDate startDate = null;

            if (startDateResult.next()) {
                Date sqlDate = startDateResult.getDate("start_date");
                if (sqlDate != null) {
                    startDate = sqlDate.toLocalDate();
                }
            }

            if (startDate == null) {
                performanceChart.getData().clear();
                return;
            }

            LocalDate oneYearAgo = LocalDate.now().minusWeeks(52);
            LocalDate effectiveStartDate = startDate.isBefore(oneYearAgo) ? oneYearAgo : startDate;
            String performanceQuery = """
            SELECT
                YEAR(activity_date) AS year,
                WEEK(activity_date, 0) AS week,
                SUM(no_flashcards) AS total_flashcards
            FROM audit
            WHERE id_user = ? AND activity_date >= ?
            GROUP BY year, week
            ORDER BY year, week;
        """;

            PreparedStatement performanceStatement = connection.prepareStatement(performanceQuery);
            performanceStatement.setInt(1, userId);
            performanceStatement.setDate(2, Date.valueOf(effectiveStartDate));

            ResultSet resultSet = performanceStatement.executeQuery();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Weekly Performance");

            int weekCounter = 1;
            while (resultSet.next()) {
                int total = resultSet.getInt("total_flashcards");
                series.getData().add(new XYChart.Data<>("Week " + weekCounter, total));
                weekCounter++;
            }

            performanceChart.getData().clear();
            performanceChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
