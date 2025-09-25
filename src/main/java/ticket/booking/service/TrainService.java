package ticket.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class TrainService {
    private static final Logger log = Logger.getLogger(TrainService.class.getName());
    private List<Train> trainList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "src/main/java/ticket/booking/localDb/trains.json";

    public TrainService() throws IOException {
        loadTrainListFromFile();
    }

    private void loadTrainListFromFile() throws IOException {
        Path filePath = Paths.get(TRAIN_DB_PATH);
        if (!Files.exists(filePath)) {
            // Create empty train list file if it doesn't exist
            Files.createDirectories(filePath.getParent());
            trainList = new ArrayList<>();
            saveTrainListToFile();
            return;
        }

        try {
            trainList = objectMapper.readValue(filePath.toFile(), new TypeReference<List<Train>>() {});
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to load trains from file, starting with empty list", e);
            trainList = new ArrayList<>();
        }
    }

    public List<Train> searchTrains(String source, String destination) {
        if (source == null || destination == null || source.trim().isEmpty() || destination.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return trainList.stream()
                .filter(train -> isValidTrain(train, source.trim(), destination.trim()))
                .toList();
    }

    public void addTrain(Train newTrain) {
        if (newTrain == null || newTrain.getTrainId() == null) {
            log.warning("Cannot add null train or train with null ID");
            return;
        }

        Optional<Train> existingTrain = trainList.stream()
                .filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId()))
                .findFirst();

        if (existingTrain.isPresent()) {
            updateTrain(newTrain);
        } else {
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }

    public void updateTrain(Train updatedTrain) {
        if (updatedTrain == null || updatedTrain.getTrainId() == null) {
            log.warning("Cannot update null train or train with null ID");
            return;
        }

        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId()))
                .findFirst();

        if (index.isPresent()) {
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        } else {
            log.info("Train not found for update, adding as new train");
            addTrain(updatedTrain);
        }
    }

    private void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to save train list to file", e);
        }
    }

    private boolean isValidTrain(Train train, String source, String destination) {
        if (train == null || train.getStations() == null || train.getStations().isEmpty()) {
            return false;
        }

        List<String> stationOrder = train.getStations();

        // Case-insensitive search for stations
        int sourceIndex = stationOrder.stream()
                .map(String::toLowerCase)
                .toList()
                .indexOf(source.toLowerCase());

        int destinationIndex = stationOrder.stream()
                .map(String::toLowerCase)
                .toList()
                .indexOf(destination.toLowerCase());

        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }
}