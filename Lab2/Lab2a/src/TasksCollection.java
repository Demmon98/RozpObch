public class TasksCollection {
    private int[][]tasks;
    private int currentTask = 0;
    private volatile boolean found;

    public TasksCollection(int[][]tasks){
        this.tasks = tasks;
        found = false;
    }

    public synchronized int[]getTask(){
        if(found || currentTask>=tasks.length)
            return null;
        else
            return tasks[currentTask++];
    }

    public synchronized void setFound(){
        this.found = true;
    }
}
