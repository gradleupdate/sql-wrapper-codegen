package org.jet.sql.codegen.wrapper.util;

import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Paths;

/**
 * @author tgorthi
 * @since December 2019
 */
public class WrapperFileUtils
{
    public static String createAndGetGeneratedClassesPath(final String packageName, final String relativeDirectoryPath, final Logger logger)
    {
        final String absoluteDirectoryPath = String.join(File.separator,
                Paths.get(relativeDirectoryPath).toAbsolutePath().toString(),
                packageName);

        final File directory = new File(absoluteDirectoryPath);
        if (!directory.exists())
        {
            boolean created = directory.mkdirs();
            if (!created)
            {
                throw new RuntimeException("Unable to create directory at : [" + directory + "]");
            }
        }

        logger.info("** ==== Writing generated classes to : [ " + absoluteDirectoryPath + "] ==== **");

        return absoluteDirectoryPath;
    }
}
