package remoteLearning.student;

import remoteLearning.Methods;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        String endProgram;
        System.out.println("\nPROGRAM DLA UCZNIA\n");
        String user = Methods.setUserName();
        do {
            switch (Methods.chooseStudentProgramOptions()) {
                case "RecordMidi" -> {
                    Methods.showMidiDevices();
                    System.out.print("\nWybor urzadzenia: ");
                    MidiDevice myDevice = Methods.chooseMidiDevice();
                    System.out.println("Wybrales urzadzenie " + myDevice.getDeviceInfo());
                    Methods.recordMidi(myDevice, user);
                }
                case "PlayYourMidiFile" -> Methods.playYourMidiFile(user);
                case "CompareTwoMidiFiles" -> Methods.compareMidiFiles(user);
                case "DisplaySoundFromMidiFile" -> Methods.displaySoundFromMidiFile(user);
                default -> System.out.println("\nWybrales niedostepna opcje");

            }
            System.out.print("\nCzy chcesz zakonczyc program? [t/n]: ");
            Scanner scanner = new Scanner(System.in);
            endProgram = scanner.next();
            System.out.println("\n");
        } while (!endProgram.equals("t"));
    }
}