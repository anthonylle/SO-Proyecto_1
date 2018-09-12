/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulador;

/**
 *
 * @author FranM
 */
public class Process {
    String name;
    Integer priority;

    public Process(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public Integer getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Process{" + "name=" + name + ", priority=" + priority + '}';
    }
    
    
}
