package br.ce.wcaquino.runners;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelRunner extends BlockJUnit4ClassRunner {

    public ParallelRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        setScheduler(new ThreadPool());
    }

    private static class ThreadPool implements RunnerScheduler {
        private ExecutorService executorService;

        public ThreadPool() {
            this.executorService = Executors.newFixedThreadPool(5);
        }

        @Override
        public void schedule(Runnable run) {
            this.executorService.submit(run);
        }

        @Override
        public void finished() {
            this.executorService.shutdown();
            try {
                this.executorService.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
