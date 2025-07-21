package com.trigger.batch.listener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.trigger.batch.model.Employee;

@Component
public class CompanyStepListener implements StepExecutionListener, EmployeeStepListener {

     private final AtomicInteger employeeWriteCount = new AtomicInteger();

    @Override
    public void notify(List<Employee> values) {
        employeeWriteCount.addAndGet(values.size());
    }

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        System.out.println("[Step START] importStep");
    }

    @Override
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        long companiesRead = stepExecution.getReadCount();
        long companiesWritten = stepExecution.getWriteCount();
        int employeesWritten = employeeWriteCount.get();

        
        System.out.println("✅ Companies read: " + companiesRead);
        System.out.println("✅ Companies written: " + companiesWritten);
        System.out.println("✅ Employees written: " + employeesWritten);

        return stepExecution.getExitStatus();
    }
  
}
