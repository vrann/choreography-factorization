package com.vrann.Choreography.Chanel;

import com.amazonaws.util.JodaTime;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.amazonaws.util.json.JSONTokener;
import com.vrann.Choreography.ChanelInterface;
import com.vrann.Choreography.MessageInterface;
import com.vrann.Choreography.SetupConfig;
import com.vrann.Factorization.Chanels;
import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Exchanger;

/**
 * Created by etulika on 6/18/16.
 */
public class FileDriver implements ChanelInterface {

    private String chanelsBasePath;

    private static int LOCK_PERIOD = 10;

    public FileDriver(String chanelsBasePath) {
        this.chanelsBasePath = chanelsBasePath;
    }

    private List<Path> getUnlockedMessages(Chanels chanel) throws Exception {
        //unlockLocked(chanelsBasePath + chanel);

        File directory = new File(chanelsBasePath + chanel);
        File[] files = directory.listFiles();

        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        ArrayList<Path> result = new ArrayList<>();
        for (File item: files) {
            Path path = Paths.get(item.toString());
            if (Files.isRegularFile(path)) {
                result.add(path);
            }
        }
        return result;
    }

    private void unlockLocked(String chanelPath) throws Exception {
        Path inflightMessages = Paths.get(chanelPath + "/inflight");

        File directory = new File(chanelPath + "/inflight");
        File[] files = directory.listFiles();

        //DirectoryStream directory = Files.newDirectoryStream(inflightMessages);
        for (File item: files) {
            Path path = Paths.get(item.getPath());
            Calendar delay = Calendar.getInstance();
            delay.roll(Calendar.SECOND, LOCK_PERIOD);
            FileTime delayTime = FileTime.from(delay.toInstant());

            if (Files.getLastModifiedTime(path).compareTo(delayTime) < 0) {
                try {
                    Path newPath = Paths.get(inflightMessages.getParent().toString() + "/" + path.getFileName());
                    Files.move(path, newPath);
                    Files.setLastModifiedTime(newPath, FileTime.fromMillis(DateTime.now().getMillis()));
                } catch (IOException e) {
                    throw new Exception();
                }
            }
        }
        //directory.close();
    }

    private Path getLockedPath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String dir = filePath.getParent().toString();
        return Paths.get(dir + "/inflight/" + fileName);
    }

    private boolean lockMessage(Path filePath) {
        Path target = getLockedPath(filePath);
        try {
            Files.move(filePath, target);
            Files.setLastModifiedTime(target, FileTime.fromMillis(DateTime.now().getMillis()));
            return true;
        } catch (Exception e) {
            System.out.println("attempted move: false");
            return false;
        }
    }

    public  MessageInterface getMessageFor(Chanels chanel) throws Exception {
        List<Path> messages = getUnlockedMessages(chanel);
        for (Path message: messages) {
            if (lockMessage(message)) {
                return new FileMessageWrapper(getLockedPath(message).toString());
            }
        }
        return null;
    }

    public List<MessageInterface> getAllMessagesFor(Chanels chanel) throws Exception {
        List<MessageInterface> result = new ArrayList<>();
        List<Path> filePaths = getUnlockedMessages(chanel);
        for (Path path: filePaths) {
            if (lockMessage(path)) {
                result.add(new FileMessageWrapper(getLockedPath(path).toString()));
            }
        }
        return result;
    }

    public void requeue(Chanels chanel, MessageInterface message) throws Exception
    {
        send(chanel, new JSONObject(new JSONTokener(message.getBody())));
        delete(chanel, message.getId());
    }

    public void send(Chanels chanel, JSONObject data) throws Exception {
        String path = SetupConfig.get().getChanelsBasePath() + chanel;
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path + "/" + UUID.randomUUID()));
        outputWriter.write(data.toString());
        outputWriter.flush();
        outputWriter.close();
    }

    public void send(Chanels chanel, JSONArray data) throws Exception {
        String path = SetupConfig.get().getChanelsBasePath() + chanel;
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path + "/" + UUID.randomUUID()));
        outputWriter.write(data.toString());
        outputWriter.flush();
        outputWriter.close();
    }

    public void delete(Chanels chanel, String messageId) throws Exception {
        //String path = SetupConfig.get().getChanelsBasePath() + chanel + "/inflight/" + messageId;
        Files.delete(Paths.get(messageId));
    }
}
