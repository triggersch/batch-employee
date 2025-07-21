package com.trigger.batch.listener;

import java.util.List;

import com.trigger.batch.model.Employee;

@FunctionalInterface
public interface EmployeeStepListener {

    void notify(List<Employee> values);

}
