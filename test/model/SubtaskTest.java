package model;

import manager.Managers;
import manager.TaskManager;
import manager.TimeOverlapException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.StatusTask.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    private TaskManager taskManager;


    @BeforeEach
    public void setUp() throws TimeOverlapException {
        taskManager = Managers.getDefault();

    }


}
