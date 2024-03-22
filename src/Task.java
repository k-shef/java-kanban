public class Task {
    final private String name;
    final private String description;

     private StatusTask status;
     private int id = 0;


    public void setId(int id) {
        this.id = id;
    }

    public Task(String name, String description, StatusTask status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public StatusTask getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

}
