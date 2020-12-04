package phone.vishnu.statussaver;

public class SaveItem {
    private boolean alreadyExists, isDone;
    private String filePath;

    public SaveItem(boolean alreadyExists, boolean isDone) {
        this.alreadyExists = alreadyExists;
        this.isDone = isDone;
    }

    public SaveItem(boolean alreadyExists, boolean isDone, String filePath) {
        this.alreadyExists = alreadyExists;
        this.isDone = isDone;
        this.filePath = filePath;
    }

    public SaveItem() {

    }

    public boolean isAlreadyExists() {
        return alreadyExists;
    }

    public void setAlreadyExists(boolean alreadyExists) {
        this.alreadyExists = alreadyExists;
    }

    public boolean alreadyExists() {
        return alreadyExists;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
