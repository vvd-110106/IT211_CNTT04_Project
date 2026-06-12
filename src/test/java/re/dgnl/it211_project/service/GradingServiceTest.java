package re.dgnl.it211_project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import re.dgnl.it211_project.model.StatusEnum;
import re.dgnl.it211_project.model.dto.GradeRequest;
import re.dgnl.it211_project.model.entity.Submission;
import re.dgnl.it211_project.repository.SubmissionRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradingServiceTest {

    @InjectMocks
    private GradingService gradingService;

    @Mock
    private SubmissionRepository submissionRepository;

    @Test
    void testGradeSubmission_Success() {
        Submission submission = new Submission();
        submission.setId(1L);
        submission.setStatus(StatusEnum.SUBMITTED);
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);
        request.setScore(95.0);
        request.setFeedback("Xuat sac");

        assertDoesNotThrow(() -> gradingService.gradeSubmission(request));
        assertEquals(StatusEnum.GRADED, submission.getStatus());
        verify(submissionRepository, times(1)).save(submission);
    }

    @Test
    void testGradeSubmission_NotFound() {
        when(submissionRepository.findById(99L)).thenReturn(Optional.empty());
        GradeRequest request = new GradeRequest();
        request.setSubmissionId(99L);

        assertThrows(RuntimeException.class, () -> gradingService.gradeSubmission(request));
    }

    @Test
    void testGradeSubmission_FailWhenPending() {
        Submission submission = new Submission();
        submission.setStatus(StatusEnum.PENDING);
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);

        assertThrows(RuntimeException.class, () -> gradingService.gradeSubmission(request));
    }

    @Test
    void testGradeSubmission_InvalidScore() {
        Submission submission = new Submission();
        submission.setStatus(StatusEnum.SUBMITTED);
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);
        request.setScore(-1.0); // Điểm âm

        assertThrows(RuntimeException.class, () -> gradingService.gradeSubmission(request));
    }

    @Test
    void testGradeSubmission_VerifyData() {
        Submission submission = new Submission();
        submission.setStatus(StatusEnum.SUBMITTED);
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        GradeRequest request = new GradeRequest();
        request.setSubmissionId(1L);
        request.setScore(80.0);
        request.setFeedback("Tot");

        gradingService.gradeSubmission(request);

        assertEquals(80.0, submission.getScore());
        assertEquals("Tot", submission.getFeedback());
        assertEquals(StatusEnum.GRADED, submission.getStatus());
    }
}