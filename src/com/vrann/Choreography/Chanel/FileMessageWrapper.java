package com.vrann.Choreography.Chanel;

import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;
import com.vrann.Choreography.MessageInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by etulika on 6/18/16.
 */
public class FileMessageWrapper implements MessageInterface {

    private String filePath;

    private String body;

    public FileMessageWrapper(String filePath) throws IOException {
        this.filePath = filePath;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(new File(this.filePath)));
            String line;
            StringBuilder fileContent = new StringBuilder();
            while ((line = fileReader.readLine()) != null) {
                fileContent.append(line);
            }
            this.body = fileContent.toString();
            fileReader.close();
        } catch (IOException e) {
            System.out.println(String.format("File %s is expected to be present", filePath));
            throw e;
        }
    }

    public String getBody() {
        return this.body;
    }

    public String getId() {
        return this.filePath;
    }
}
