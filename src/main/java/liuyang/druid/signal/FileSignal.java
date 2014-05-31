package liuyang.druid.signal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSignal implements Signal {
    Path filePath;

    public FileSignal(String fileName) {
        this.filePath = Paths.get(fileName).toAbsolutePath();
    }

    public FileSignal(Path path) {
        this.filePath = path.toAbsolutePath();
    }

    public Path getFilePath() {
        return filePath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((filePath == null) ? 0 : filePath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileSignal other = (FileSignal) obj;
        if (filePath == null) {
            if (other.filePath != null)
                return false;
        } else if (!filePath.equals(other.filePath))
            return false;
        return true;
    }

    @Override
    public Object value() {
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return "FileSignal [filePath=" + filePath + "]";
    }

    @Override
    public String identity() {
        return String.format("@file('%s')", filePath);
    }

}
