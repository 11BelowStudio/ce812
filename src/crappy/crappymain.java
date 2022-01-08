package crappy;

import java.util.Scanner;

/**
 * A main method which exists to hopefully get Crappy, by itself, jarable.
 * @author Rachel Lowe
 */
public class crappymain {
    /*
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at https://mozilla.org/MPL/2.0/.
     */


    public static void main(String[] args){

        Scanner s = new Scanner(System.in);

        System.out.println("It appears that you're trying to run the Cool Realism-Adjacent Physics Package Y'know as an executable jar.");
        System.out.println("Unfortunately, CRAPPY, by itself, is not an executable.");
        System.out.println("");
        System.out.println("CRAPPY is a library for high-quality CRAP (Cool Realism-Adjacent Physics)");
        System.out.println("so feel free to include CRAPPY in your work.");
        System.out.println("");
        System.out.println("- Rachel Lowe, 2022");
        System.out.println("Please note: CRAPPY's source code is licensed under the terms of the Mozilla Public License 2.0");
        System.out.println("https://mozilla.org/MPL/2.0/");
        System.out.println("");
        System.out.println("");
        pressQToQuit(s);
        System.out.println("");
        System.out.println("LEGAL STUFF: ");
        System.out.println("");
        System.out.println();

    }

    private static void pressQToQuit(final Scanner s){
        System.out.println("If you wish to close this wall of text, please type anything then press enter");
        System.out.println("Otherwise, please just press enter, and leave the input blank to continue.");

        if (!s.nextLine().isEmpty()){
            System.out.println("ok bye");
            System.exit(0);
        }

    }
}
