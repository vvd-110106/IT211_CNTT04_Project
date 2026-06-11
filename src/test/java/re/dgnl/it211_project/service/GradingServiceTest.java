package re.dgnl.it211_project.service;

import re.dgnl.it211_project.model.StatusEnum;
import re.dgnl.it211_project.model.dto.GradeRequest;
import re.dgnl.it211_project.model.entity.Submission;
import re.dgnl.it211_project.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GradingServiceTest {
    @InjectMocks
    private GradingService gradingService;

    @Mock
    private SubmissionRepository submissionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGrade_Fail_WhenPending() {
        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);
        request.setScore(85.0);

        Submission mockSubmission = Submission.builder().id(1L).status(StatusEnum.PENDING).build();
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(mockSubmission));

        assertThrows(RuntimeException.class, () -> gradingService.gradeSubmission(request));
    }
}