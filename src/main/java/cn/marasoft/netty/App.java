package cn.marasoft.netty;

import cn.marasoft.netty.server.ChatServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        new ChatServer().start();
    }
}
