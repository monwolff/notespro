package com.example.notespro;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.util.Util;

import org.w3c.dom.Text;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText;
    EditText contentEditText;
    ImageButton saveNoteBtn;
    ImageButton deleteNoteBtn;
    TextView pageTitleTextView;
    String title, content, docId;
    boolean isEditMode = false;
    //TextView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById((R.id.page_title));
        //deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);
        deleteNoteBtn = findViewById(R.id.delete_note_btn);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode){
            pageTitleTextView.setText("Edit your note");
            //deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
            deleteNoteBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v)-> saveNote());

        //deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());
        deleteNoteBtn.setOnClickListener((v)-> deleteNoteFromFirebase());

    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if (noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){
            //update note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //note is successful
                    Utility.showToast(NoteDetailsActivity.this, "Note added successfully");
                    finish();
                } else{
                    Utility.showToast(NoteDetailsActivity.this, "Failed while adding note");
                    //not has failed

                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

            documentReference = Utility.getCollectionReferenceForNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NoteDetailsActivity.this, "Note deleted successfully");
                    finish();
                } else{
                    Utility.showToast(NoteDetailsActivity.this, "Failed while deleting note");
                    //not has failed

                }
            }
        });

    }
}