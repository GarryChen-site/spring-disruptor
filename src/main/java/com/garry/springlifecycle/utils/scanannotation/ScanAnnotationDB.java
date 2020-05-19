package com.garry.springlifecycle.utils.scanannotation;



import org.scannotation.AnnotationDB;
import org.scannotation.archiveiterator.Filter;
import org.scannotation.archiveiterator.StreamIterator;

import java.io.InputStream;
import java.net.URL;

public class ScanAnnotationDB extends AnnotationDB {

    public void scanArchives(URL... urls) {
        for (URL url : urls) {
            Filter filter = new Filter() {
                @Override
                public boolean accepts(String filename) {
                    if (filename.endsWith(".class")) {
                        if (filename.startsWith("/")) {
                            filename = filename.substring(1);
                        }
                        if (!ignoreScan(filename.replace('/', '.'))) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            try {
                final StreamIterator it = IteratorFactory.create(url, filter);
                InputStream stream;
                while ((stream = it.next()) != null) {
                    scanClass(stream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean ignoreScan(String intf) {
        for (String ignogred : ignoredPackages) {
            if (intf.startsWith(ignogred + ".")) {
                return true;
            }
        }
        return false;
    }

}
