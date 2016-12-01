package tech.android.tcmp13.filesdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_PERMISSION = 169;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String FILE_NAME = "shuki.txt";

    private EditText sheldonCooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sheldonCooper = (EditText) findViewById(R.id.drSheldonCooper);
    }

    /**
     * Save the user input to a file
     *
     * @param view the clicked view
     */
    public void saveClickListener(View view) {

        askForPermission();
        if (isExternalStorageAvailable())
            writeToExternalStorage();
        else
            writeToInternalStorage();
    }

    private void writeToExternalStorage() {

        File file = getFile(FILE_NAME, Environment.DIRECTORY_DOWNLOADS);
        if (file == null)
            return;
        try {
            write(new FileOutputStream(file));
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void writeToInternalStorage() {

        try {
            write(openFileOutput(FILE_NAME, MODE_PRIVATE));
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void write(FileOutputStream out) throws IOException {

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(sheldonCooper.getText().toString());
            writer.flush();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /**
     * Load the user input from a file
     *
     * @param view the clicked view
     */

    public void loadClickListener(View view) {

        askForPermission();
        if (isExternalStorageAvailable())
            readFromExternalStorage();
        else
            readFromInternalStorage();
    }

    private void readFromExternalStorage() {

        File file = getFile(FILE_NAME, Environment.DIRECTORY_DOWNLOADS);
        if (file == null)
            return;
        try {
            sheldonCooper.setText(read(new FileInputStream(file)));
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void readFromInternalStorage() {

        try {
            sheldonCooper.setText(read(openFileInput(FILE_NAME)));
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @NonNull
    private String read(FileInputStream in) throws IOException {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            builder.append(reader.readLine()).append("\n");
        } finally {
            if (reader != null)
                reader.close();
        }
        return builder.toString();
    }

    private boolean isExternalStorageAvailable() {

        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private File getFile(String fileName, String parentDirectory) {

        File file = new File(Environment.getExternalStoragePublicDirectory(parentDirectory), fileName);

        //Check the parenthood of this bastardo file
        if (!file.getParentFile().exists()) {
            boolean mkdirsSuccess = file.mkdirs();
            if (!mkdirsSuccess) {
                Toast.makeText(this, "Failed To Get the Downloads Folder", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        //Check out the file
        if (!file.exists()) {
            try {
                boolean createNewFileSuccess = file.createNewFile();
                if (!createNewFileSuccess) {
                    Toast.makeText(this, "Failed To Create The New shuki.txt file", Toast.LENGTH_SHORT).show();
                    return null;
                }
            } catch (IOException e) {
                Toast.makeText(this, "Failed To Create The New shuki.txt file", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        return file;
    }

    private void askForPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            if (checkSelfPermission(writeExternalStorage) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{writeExternalStorage}, WRITE_EXTERNAL_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != WRITE_EXTERNAL_PERMISSION)
            return;
        //index is 0 because we asked for 1 permission, if there were more iterate over the permissions and check the grant results.
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            //There's a permission from the user, saveClickListener the file
        } else {
            Toast.makeText(this, "You are the worst, please die", Toast.LENGTH_LONG).show();
        }
    }
}
