package org.example.demo.monitor;

import io.airlift.airline.Command;

import java.util.concurrent.Callable;

/**
 * Only used to display help information
 * @since 2020/9/7
 * @author phial
 */
@Command(name = "exit/quit", description = "Exit current session")
public abstract class ExitCommandHandler implements Callable<String> {
}