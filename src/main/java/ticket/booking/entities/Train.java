package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Train {
    private String trainId;
    private String trainNo;
    private List<List<Integer>> seats;
    private Map<String, String> stationTimes;
    private List<String> stations;

    // Default constructor
    public Train() {}

    // Full constructor
    public Train(String trainId, String trainNo, List<List<Integer>> seats,
                 Map<String, String> stationTimes, List<String> stations) {
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
        this.stationTimes = stationTimes;
        this.stations = stations;
    }

    // Builder pattern implementation
    public static TrainBuilder builder() {
        return new TrainBuilder();
    }

    public static class TrainBuilder {
        private String trainId;
        private String trainNo;
        private List<List<Integer>> seats;
        private Map<String, String> stationTimes;
        private List<String> stations;

        public TrainBuilder trainId(String trainId) {
            this.trainId = trainId;
            return this;
        }

        public TrainBuilder trainNo(String trainNo) {
            this.trainNo = trainNo;
            return this;
        }

        public TrainBuilder seats(List<List<Integer>> seats) {
            this.seats = seats;
            return this;
        }

        public TrainBuilder stationTimes(Map<String, String> stationTimes) {
            this.stationTimes = stationTimes;
            return this;
        }

        public TrainBuilder stations(List<String> stations) {
            this.stations = stations;
            return this;
        }

        public Train build() {
            return new Train(trainId, trainNo, seats, stationTimes, stations);
        }
    }

    // Getters and Setters
    public String getTrainId() { return trainId; }
    public void setTrainId(String trainId) { this.trainId = trainId; }

    public String getTrainNo() { return trainNo; }
    public void setTrainNo(String trainNo) { this.trainNo = trainNo; }

    public List<List<Integer>> getSeats() { return seats; }
    public void setSeats(List<List<Integer>> seats) { this.seats = seats; }

    public Map<String, String> getStationTimes() { return stationTimes; }
    public void setStationTimes(Map<String, String> stationTimes) { this.stationTimes = stationTimes; }

    public List<String> getStations() { return stations; }
    public void setStations(List<String> stations) { this.stations = stations; }

    public String getTrainInfo() {
        return String.format("Train ID: %s Train No: %s", trainId, trainNo);
    }
}