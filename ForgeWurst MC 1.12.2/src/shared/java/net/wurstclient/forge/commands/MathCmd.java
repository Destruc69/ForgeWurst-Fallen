package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public final class MathCmd extends Command {
    public MathCmd() {
        super("math", "Perform mathematical operations.",
                "Syntax: .math <expression>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        StringBuilder expression = new StringBuilder();
        for (String arg : args) {
            if (arg != null) {
                expression.append(arg).append(" ");
            }
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        try {
            double result = (double) engine.eval(expression.toString().trim());
            ChatUtils.message(expression.toString().trim() + " = " + result);
        } catch (ScriptException ignored) {
        }
    }
}