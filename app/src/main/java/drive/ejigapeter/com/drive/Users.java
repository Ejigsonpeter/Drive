package drive.ejigapeter.com.drive;




public class Users {

    private String email,status;

    public  Users(){


    }
    public Users(String email, String status){
        this.email = email;
        this.status = status;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

