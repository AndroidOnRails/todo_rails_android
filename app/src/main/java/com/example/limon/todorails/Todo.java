package com.example.limon.todorails;


public class Todo {
    public String text;
    public int id;
    public int project_id;
    public boolean isCompleted;

    Todo() {
        this.isCompleted = false;
    }
}
