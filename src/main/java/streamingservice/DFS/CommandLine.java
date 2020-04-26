package streamingservice.DFS;

import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.nio.file.*;


public class CommandLine {
    private DFS dfs;

    public CommandLine(int p, int portToJoin) throws Exception {
        // User interface:
        // join, ls, touch, delete, read, tail, head, append, move
        dfs = new DFS(p);

        if (portToJoin > 0) {
            System.out.println("Joining " + portToJoin);
            dfs.join("127.0.0.1", portToJoin);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (!line.equals("quit")) {
            String[] result = line.split("\\s");
            if (result[0].equalsIgnoreCase("join") && result.length > 1) {
                dfs.join("127.0.0.1", Integer.parseInt(result[1]));
            } else if (result[0].equalsIgnoreCase("ls")) {
                dfs.ls();
            } else if (result[0].equalsIgnoreCase("touch") && result.length > 1) {
                dfs.touch(result[1]);
            } else if (result[0].equalsIgnoreCase("delete") && result.length > 1) {
                dfs.delete(result[1]);
            } else if (result[0].equalsIgnoreCase("read") && result.length > 2) {
                dfs.read(result[1], Integer.parseInt(result[2]));
            } else if (result[0].equalsIgnoreCase("tail") && result.length > 1) {
                dfs.tail(result[1]);
            } else if (result[0].equalsIgnoreCase("head") && result.length > 1) {
                dfs.head(result[1]);
            } else if (result[0].equalsIgnoreCase("append") && result.length > 2) {
                byte[] bdata = Base64.getDecoder().decode(result[2]);
                Byte[] bytes = new Byte[bdata.length];
                int i = 0;
                for (byte b : bdata) bytes[i++] = b;
                dfs.append(result[1], bytes);
            } else if (result[0].equalsIgnoreCase("move") && result.length > 2) {
                dfs.mv(result[1], result[2]);
            } else if (result[0].equalsIgnoreCase("print")) {
                dfs.print();
            } else if (result[0].equalsIgnoreCase("leave")) {
                dfs.leave();
            }
            line = br.readLine();
        }
    }
    
    static public void main(String args[]) throws Exception
    {
        if (args.length < 1 ) {
            throw new IllegalArgumentException("Parameter: <port> <portToJoin>");
        }
        if (args.length > 1) {
            CommandLine dfscl = new CommandLine(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } else {
            CommandLine commandLine = new CommandLine(Integer.parseInt(args[0]), 0);
        }
     } 
}
