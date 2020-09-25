package phone.vishnu.statussaver;

public class BooleanItem {
    boolean alreadyExists, isDone;

    public BooleanItem() {
    }

    public BooleanItem(boolean alreadyExists, boolean isDone) {
        this.alreadyExists = alreadyExists;
        this.isDone = isDone;
    }

    public boolean alreadyExists() {
        return alreadyExists;
    }

    public void setAlreadyExists(boolean alreadyExists) {
        this.alreadyExists = alreadyExists;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
