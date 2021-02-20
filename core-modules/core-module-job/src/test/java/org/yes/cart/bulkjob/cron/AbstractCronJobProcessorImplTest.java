package org.yes.cart.bulkjob.cron;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Job;
import org.yes.cart.domain.entity.JobDefinition;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.JobDefinitionService;
import org.yes.cart.service.domain.JobService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * User: denispavlov
 * Date: 20/02/2021
 * Time: 07:50
 */
public class AbstractCronJobProcessorImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void processPaused() throws Exception {

        final JobService jobService = this.context.mock(JobService.class);
        final JobDefinitionService jobDefinitionService = this.context.mock(JobDefinitionService.class);

        final JobStatus status = this.context.mock(JobStatus.class);
        final Instant checkpoint = null;
        final Map<String, Object> jobCtx = new HashMap<>();
        jobCtx.put("jobId", 123L);
        jobCtx.put("jobDefinitionId", 234L);
        jobCtx.put("jobName", "TEST");

        final Job job = this.context.mock(Job.class);

        final AbstractCronJobProcessorImpl processor = new AbstractCronJobProcessorImpl() {
            @Override
            protected Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {
                fail("Must not execute paused jobs");
                return new Pair<>(status, checkpoint);
            }

            @Override
            public JobStatus getStatus(final String token) {
                return status;
            }
        };

        processor.setJobService(jobService);
        processor.setJobDefinitionService(jobDefinitionService);

        this.context.checking(new Expectations() {{
            oneOf(jobService).getById(123L); will(returnValue(job));
            exactly(2).of(job).getPaused(); will(returnValue(true));
        }});

        processor.process(jobCtx);

    }

    @Test
    public void processSuccessNoCheckpoint() throws Exception {

        final JobService jobService = this.context.mock(JobService.class);
        final JobDefinitionService jobDefinitionService = this.context.mock(JobDefinitionService.class);

        final JobStatus status = this.context.mock(JobStatus.class);
        final Instant checkpoint = null;
        final Map<String, Object> jobCtx = new HashMap<>();
        jobCtx.put("jobId", 123L);
        jobCtx.put("jobDefinitionId", 234L);
        jobCtx.put("jobName", "TEST");

        final Job job = this.context.mock(Job.class);
        final JobDefinition definition = this.context.mock(JobDefinition.class);

        final AbstractCronJobProcessorImpl processor = new AbstractCronJobProcessorImpl() {
            @Override
            protected Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job jobToProcess, final JobDefinition definitionToProcess) {
                assertSame(jobCtx, context);
                assertSame(job, jobToProcess);
                assertSame(definition, definitionToProcess);
                return new Pair<>(status, checkpoint);
            }

            @Override
            public JobStatus getStatus(final String token) {
                return status;
            }
        };

        processor.setJobService(jobService);
        processor.setJobDefinitionService(jobDefinitionService);

        this.context.checking(new Expectations() {{
            oneOf(jobService).getById(123L); will(returnValue(job));
            allowing(job).getPaused(); will(returnValue(false));
            oneOf(jobDefinitionService).getById(234L); will(returnValue(definition));
            // Job is updated
            oneOf(job).setLastRun(with(any(Instant.class)));
            allowing(status).getCompletion(); will(returnValue(JobStatus.Completion.OK));
            oneOf(job).setLastState(JobStatus.Completion.OK.name());
            oneOf(job).setLastDurationMs(with(any(long.class)));
            allowing(status).getReport(); will(returnValue("Report"));
            oneOf(job).setLastReport("Report");
            oneOf(job).setCheckpoint(null);
            oneOf(jobService).update(job);
        }});

        processor.process(jobCtx);

    }

    @Test
    public void processErrorNoCheckpointNoPauseOnError() throws Exception {

        final JobService jobService = this.context.mock(JobService.class);
        final JobDefinitionService jobDefinitionService = this.context.mock(JobDefinitionService.class);

        final JobStatus status = this.context.mock(JobStatus.class);
        final Instant checkpoint = null;
        final Map<String, Object> jobCtx = new HashMap<>();
        jobCtx.put("jobId", 123L);
        jobCtx.put("jobDefinitionId", 234L);
        jobCtx.put("jobName", "TEST");

        final Job job = this.context.mock(Job.class);
        final JobDefinition definition = this.context.mock(JobDefinition.class);

        final AbstractCronJobProcessorImpl processor = new AbstractCronJobProcessorImpl() {
            @Override
            protected Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job jobToProcess, final JobDefinition definitionToProcess) {
                assertSame(jobCtx, context);
                assertSame(job, jobToProcess);
                assertSame(definition, definitionToProcess);
                return new Pair<>(status, checkpoint);
            }

            @Override
            public JobStatus getStatus(final String token) {
                return status;
            }
        };

        processor.setJobService(jobService);
        processor.setJobDefinitionService(jobDefinitionService);

        this.context.checking(new Expectations() {{
            oneOf(jobService).getById(123L); will(returnValue(job));
            allowing(job).getPaused(); will(returnValue(false));
            oneOf(jobDefinitionService).getById(234L); will(returnValue(definition));
            // Job is updated
            oneOf(job).setLastRun(with(any(Instant.class)));
            allowing(status).getCompletion(); will(returnValue(JobStatus.Completion.ERROR));
            oneOf(job).setLastState(JobStatus.Completion.ERROR.name());
            oneOf(job).setLastDurationMs(with(any(long.class)));
            allowing(status).getReport(); will(returnValue("Report"));
            oneOf(job).setLastReport("Report");
            oneOf(job).setCheckpoint(null);
            allowing(job).getPauseOnError(); will(returnValue(false));
            oneOf(jobService).update(job);
        }});

        processor.process(jobCtx);

    }

    @Test
    public void processErrorNoCheckpointPauseOnError() throws Exception {

        final JobService jobService = this.context.mock(JobService.class);
        final JobDefinitionService jobDefinitionService = this.context.mock(JobDefinitionService.class);

        final JobStatus status = this.context.mock(JobStatus.class);
        final Instant checkpoint = null;
        final Map<String, Object> jobCtx = new HashMap<>();
        jobCtx.put("jobId", 123L);
        jobCtx.put("jobDefinitionId", 234L);
        jobCtx.put("jobName", "TEST");

        final Job job = this.context.mock(Job.class);
        final JobDefinition definition = this.context.mock(JobDefinition.class);

        final AbstractCronJobProcessorImpl processor = new AbstractCronJobProcessorImpl() {
            @Override
            protected Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job jobToProcess, final JobDefinition definitionToProcess) {
                assertSame(jobCtx, context);
                assertSame(job, jobToProcess);
                assertSame(definition, definitionToProcess);
                return new Pair<>(status, checkpoint);
            }

            @Override
            public JobStatus getStatus(final String token) {
                return status;
            }
        };

        processor.setJobService(jobService);
        processor.setJobDefinitionService(jobDefinitionService);

        this.context.checking(new Expectations() {{
            oneOf(jobService).getById(123L); will(returnValue(job));
            allowing(job).getPaused(); will(returnValue(false));
            oneOf(jobDefinitionService).getById(234L); will(returnValue(definition));
            // Job is updated
            oneOf(job).setLastRun(with(any(Instant.class)));
            allowing(status).getCompletion(); will(returnValue(JobStatus.Completion.ERROR));
            oneOf(job).setLastState(JobStatus.Completion.ERROR.name());
            oneOf(job).setLastDurationMs(with(any(long.class)));
            allowing(status).getReport(); will(returnValue("Report"));
            oneOf(job).setLastReport("Report");
            oneOf(job).setCheckpoint(null);
            allowing(job).getPauseOnError(); will(returnValue(true));
            oneOf(job).setPaused(true);
            oneOf(jobService).update(job);
        }});

        processor.process(jobCtx);

    }

    @Test
    public void processSuccessWithCheckpoint() throws Exception {

        final JobService jobService = this.context.mock(JobService.class);
        final JobDefinitionService jobDefinitionService = this.context.mock(JobDefinitionService.class);

        final JobStatus status = this.context.mock(JobStatus.class);
        final Instant checkpoint = Instant.now();
        final Map<String, Object> jobCtx = new HashMap<>();
        jobCtx.put("jobId", 123L);
        jobCtx.put("jobDefinitionId", 234L);
        jobCtx.put("jobName", "TEST");

        final Job job = this.context.mock(Job.class);
        final JobDefinition definition = this.context.mock(JobDefinition.class);

        final AbstractCronJobProcessorImpl processor = new AbstractCronJobProcessorImpl() {
            @Override
            protected Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job jobToProcess, final JobDefinition definitionToProcess) {
                assertSame(jobCtx, context);
                assertSame(job, jobToProcess);
                assertSame(definition, definitionToProcess);
                return new Pair<>(status, checkpoint);
            }

            @Override
            public JobStatus getStatus(final String token) {
                return status;
            }
        };

        processor.setJobService(jobService);
        processor.setJobDefinitionService(jobDefinitionService);

        this.context.checking(new Expectations() {{
            oneOf(jobService).getById(123L); will(returnValue(job));
            allowing(job).getPaused(); will(returnValue(false));
            oneOf(jobDefinitionService).getById(234L); will(returnValue(definition));
            // Job is updated
            oneOf(job).setLastRun(with(any(Instant.class)));
            allowing(status).getCompletion(); will(returnValue(JobStatus.Completion.OK));
            oneOf(job).setLastState(JobStatus.Completion.OK.name());
            oneOf(job).setLastDurationMs(with(any(long.class)));
            allowing(status).getReport(); will(returnValue("Report"));
            oneOf(job).setLastReport("Report");
            oneOf(job).setCheckpoint(checkpoint);
            oneOf(jobService).update(job);
        }});

        processor.process(jobCtx);

    }

}