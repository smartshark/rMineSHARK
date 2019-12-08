package de.ugoe.cs.smartshark.rMineSHARK.util;

import com.mongodb.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A collection of common used helper methods.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class Common {

  /**
   * This method compares the JVM method signature provided by SourceMeter withe the
   * simple method name provided by RefDiff. If both names represents the same method,
   * this method returns true.
   * @param mSourceMeter The JVM method signature provided by SourceMeter.
   * @param mRefDiff The simple method signature provided by RefDiff.
   * @return If both names represents the same method, his method returns true.
   * Otherwise false.
   */
  public static boolean compareMethods(String mSourceMeter, String mRefDiff) {
    // simplify SourceMeter name
    String name = mSourceMeter.split("\\(")[0];
    String signature = mSourceMeter.split("\\(")[1].split("\\)")[0];
    String simpleSignature = simplifyJavaVMMethodSignature(signature);
    String sm = (name + simpleSignature).replace(" ", "");

    String rd = mRefDiff.replace(" ", "");

    return sm.equals(rd);
  }

  /*
   * Simplifies a JVM signature. It cuts path information of passed types and
   * reformats array brackets.
   */
  private static String simplifyJavaVMMethodSignature(String signature) {
    StringBuilder simpleName = new StringBuilder();
    for (int i = 0; i < signature.length(); ++i) {
      StringBuilder type = new StringBuilder();
      if (signature.charAt(i) == '[') {
        if (signature.charAt(i + 1) == 'L') {
          int j = getTypeName(type, signature, i + 2);
          i = j;
          type.append("[]");
        } else {
          type.append(getJVMPrimitiveType(signature.charAt(i + 1)));
          type.append("[]");
          i += 1;
        }
      } else if (signature.charAt(i) == 'L') {
        int j = getTypeName(type, signature, i + 1);
        i = j;
      } else {
        type.append(getJVMPrimitiveType(signature.charAt(i)));
      }
      if (simpleName.length() == 0) {
        simpleName.append("(");
        simpleName.append(type);
      } else {
        simpleName.append(", ");
        simpleName.append(type);
      }
    }
    if (simpleName.length() == 0) {
      simpleName.append("(");
    }
    simpleName.append(")");
    return simpleName.toString();
  }

  private static int getTypeName(StringBuilder type, String name, int start) {
    String typeName = "";
    for (int j = start; j < name.length(); j++) {
      if (name.charAt(j) == ';') {
        type.append(simpleTypeName(typeName));
        return j;
      } else {
        typeName += name.charAt(j);
      }
    }
    return start + 1;
  }

  private static String getJVMPrimitiveType(char type) {
    switch (type) {
      case 'Z':
        return "boolean";
      case 'B':
        return "byte";
      case 'C':
        return "char";
      case 'S':
        return "short";
      case 'I':
        return "int";
      case 'J':
        return "long";
      case 'F':
        return "float";
      case 'D':
        return "double";
      case 'V':
        return "void";
      default:
        return "";
    }
  }

  private static String simpleTypeName(String name) {
    String[] parts = name.split("/");
    return parts[parts.length - 1];
  }

  /**
   * Reformats the type name with a '$' as separator for nested types.
   * @param name The type name to be reformatted.
   * @param level The nesting level.
   * @return The reformatted type name with a '$' as separator for nested types.
   */
  public static String formatNestedTypeName(String name, int level) {
    String parts[] = name.split("\\.");
    StringBuilder formattedName = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      formattedName.append(parts[i]);
      if (i < parts.length - (1 + level)) {
        formattedName.append(".");
      } else if (i < parts.length - 1) {
        formattedName.append("$");
      }
    }
    return formattedName.toString();
  }

  public static void loadRepoFromMongoDB(String projectName, MongoClient client) throws IOException {
    GridFSBucket gridFSBucket = GridFSBuckets.create(client.getDatabase("smartshark"),"repository_data");
    File yourFile = new File("tmp/"+ projectName + ".tar.gz");
    yourFile.createNewFile();
    FileOutputStream streamToDownloadTo = new FileOutputStream(yourFile);
    gridFSBucket.downloadToStream(projectName + ".tar.gz", streamToDownloadTo);
    streamToDownloadTo.close();
    extractTarGZ(yourFile,"tmp");
    yourFile.delete();
  }

  public static void extractTarGZ(File archive, String out) throws IOException {
    File destination = new File(out);
    Archiver archiver = ArchiverFactory.createArchiver("tar", "gz");
    archiver.extract(archive, destination);
  }
}
