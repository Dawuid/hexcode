/*
 * Copyright (c) 2013 David Santos
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.dawuid.hexcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

/**
 * Tool for translate hex strings to binary.
 * @author Dawuid
 */
public class HexCode {

	private InputStream is;
	private PrintStream os;
	private PrintStream err;

	private boolean modeVerbose = false;

	private HexCode(InputStream is, PrintStream os, PrintStream err) {
		this.is = is;
		this.os = os;
		this.err = err;
	}

	private int readHexChar(Reader reader, char[] out, int off)
			throws IOException {
		char[] buffer = new char[1];
		int result = reader.read(buffer);
		while (result != -1) {
			if (Character.isLetterOrDigit(buffer[0])) {
				out[off] = buffer[0];
				break;
			}
			result = reader.read(buffer);
		}
		return result;
	}

	private int readHex(Reader reader) throws IOException {
		int result = 0;
		char[] hex = new char[2];
		result = readHexChar(reader, hex, 0);
		if (result != -1) {
			result = readHexChar(reader, hex, 1);
			if (result == -1) {
				throw new IOException(
						"EOF reached in middle of hexadecimal representation");
			}
		}
		if (result != -1) {
			try {
				result = Integer.parseInt(String.copyValueOf(hex), 16);
				if ((result < 0) && (result > 256)) {
					throw new IOException(
							"Byte value obtained is not in a valid range: "
									+ result);
				}
			} catch (NumberFormatException ex) {
				throw new IOException(
						"Readed data is not in hexadecimal representation: "
								+ String.valueOf(hex));
			}
		}

		return result;
	}

	private void toBinary() {
		Reader reader = new BufferedReader(new InputStreamReader(is));
		try {
			byte[] byteRecovered = new byte[1];
			int readed = readHex(reader);
			while (readed > -1) {
				byteRecovered[0] = (byte) readed;
				os.write(byteRecovered);
				readed = readHex(reader);
			}
			os.flush();
		} catch (IOException ex) {
			err.println("Error procesing data: " + ex.getMessage());
			if (modeVerbose) {
				ex.printStackTrace(err);
			}
		}
	}

	private void toHex() {

	}

	private void printHelp() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HexCode coder = new HexCode(System.in, System.out, System.err);
		if (args.length == 1) {
			switch (args[0]) {
			case "hex":
				coder.toHex();
				break;
			case "bin":
				coder.toBinary();
				break;
			default:
				coder.printHelp();
			}
		} else {
			coder.printHelp();
		}
	}

}
