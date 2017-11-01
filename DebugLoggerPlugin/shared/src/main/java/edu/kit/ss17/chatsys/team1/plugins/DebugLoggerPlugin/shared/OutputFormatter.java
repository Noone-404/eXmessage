package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import static org.fusesource.jansi.Ansi.ansi;

/**
 *
 */
class OutputFormatter {

	private static final int PLACE_SPACE = 44;

	private OutputFormatter() {
	}

	static String render(String place, String data, boolean isXML, boolean isIncoming) {
		Ansi renderedValue = ansi()
				.reset().a("[ ")
				.fgBrightBlue().a(String.format("%1$-" + PLACE_SPACE + 's', place))
				.reset().a(" ] Size: ").a(humanReadableByteCount(data.trim().getBytes().length, false)).a(System.lineSeparator());

		if (isXML)
			renderedValue = renderedValue.fgBright(isIncoming ? Color.YELLOW : Color.GREEN);
		else
			renderedValue = renderedValue.fg(isIncoming ? Color.YELLOW : Color.GREEN);

		return renderedValue.a(data.trim()).reset().toString();
	}

	static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
