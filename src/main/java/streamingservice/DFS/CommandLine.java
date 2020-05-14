package streamingservice.DFS;

import java.util.*;
import java.io.*;


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
                RemoteInputFileStream rifs = dfs.read(result[1], Integer.parseInt(result[2]));
                rifs.connect();
                int nextByte = rifs.read();
                while (nextByte != -1) {
                    System.out.print((char) nextByte);
                    nextByte = rifs.read();
                }
                System.out.println();
                System.out.println("Done");
            } else if (result[0].equalsIgnoreCase("tail") && result.length > 1) {
                dfs.tail(result[1]);
            } else if (result[0].equalsIgnoreCase("head") && result.length > 1) {
                dfs.head(result[1]);
            } else if (result[0].equalsIgnoreCase("append") && result.length > 2) {
                dfs.append(result[1], new RemoteInputFileStream(result[2]));
            } else if (result[0].equalsIgnoreCase("mv") && result.length > 2) {
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
