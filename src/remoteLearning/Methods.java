package remoteLearning;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Methods {
    public static String chooseTeacherProgramOptions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Opcje programu
                1. Nagranie utworu dla ucznia i zapisanie go
                2. Odtworzenie pliku .midi z pulpitu""");
        System.out.print("Wybierz co chcesz zrobic: ");
        int choice = scanner.nextInt();
        if (choice == 1 ) {
            return "RecordMidi";
        } else if (choice == 2) {
            return "PlayYourMidiFile";
        } else {
            return "";
        }
    }

    public static String chooseStudentProgramOptions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                Opcje programu
                1. Nagranie utworu i zapisanie go
                2. Odtworzenie pliku .midi z pulpitu
                3. Porownanie dwoch plikow .midi
                4. Wyswietlenie dzwiekow wystepujacych w pliku .midi""");
        System.out.print("Wybierz co chcesz zrobic: ");
        int choice = scanner.nextInt();
        if (choice == 1 ) {
            return "RecordMidi";
        } else if (choice == 2) {
            return "PlayYourMidiFile";
        }else if (choice == 3) {
            return "CompareTwoMidiFiles";
        } else if (choice == 4) {
            return "DisplaySoundFromMidiFile";
        } else {
            return "";
        }
    }

    public static void playYourMidiFile(String user) throws InvalidMidiDataException, IOException,
            MidiUnavailableException {
        Scanner scanner  = new Scanner(System.in);
        System.out.print("\nPodaj nazwe pliku z pulpitu, ktory chcesz odtworzyc: ");
        String fileName = scanner.next();
        String fileNameString = "C:\\Users\\" + user + "\\Desktop\\" + fileName + ".mid";
        File file = new File(fileNameString);
        Sequence sequence = MidiSystem.getSequence(file);
        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.setSequence(sequence);
        sequencer.start();
        System.out.print("\nTrwa odtwarzanie...");
        while (true){
            if (!sequencer.isRunning()) {
                break;
            }
        }
        sequencer.close();
    }

    public static void showMidiDevices() {
        System.out.println("\nDOSTEPNE URZADZENIA MIDI\n");
        MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
        for (int i = 4; i < devices.length; i++) {
            System.out.println("\t" + (i -3) + ". " + devices[i].getName() + " - " + devices[i].getDescription());
        }
        System.out.println("""
                        Z dostepnych urzadzen wybierz to na ktorym chcesz nagrac utwor
                        Upewnij sie ze na pewno zainstalowales najnowsze sterowniki
                        Jezeli nie widzisz swojego urzadzenia na liscie sprobuj podpiac je jeszcze raz
                        i uruchom program ponownie""");
    }

    public static MidiDevice chooseMidiDevice() throws MidiUnavailableException {
        Scanner scanner = new Scanner(System.in);
        MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
        return MidiSystem.getMidiDevice(devices[scanner.nextInt() + 3]);
    }

    public static void recordMidi(MidiDevice myDevice, String user) throws MidiUnavailableException,
            InvalidMidiDataException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Gdy bedziesz gotowy do nagrywania nacisnij [s]: ");
        String startRecord = scanner.next();
        while(!startRecord.equals("s")) {
            startRecord = scanner.next();
        }
        Sequencer sequencer = setSequencer(myDevice);
        sequencer.startRecording();
        System.out.println("\nNagrywanie rozpoczete");
        System.out.println("\nRecording...\n");

        if(sequencer.isRecording()) {
            System.out.print("Gdy zakonczysz nagrywanie nacisnij [s]: ");
            String endRecord = scanner.next();
            while(!endRecord.equals("s")) {
                endRecord = scanner.next();
            }
            sequencer.stopRecording();
            System.out.println("\nNagrywanie zakonczone\n");
        }
        System.out.print("Czy chcesz odtworzyć nagrany plik? [t/n]: ");
        String playYourRecord = scanner.next();
        if (playYourRecord.equals("t")) {
            sequencer.start();
            System.out.print("\nOdtwarzanie...");
            while (true){
                if (!sequencer.isRunning()) {
                    break;
                }
            }
        }
        System.out.print("\nCzy chcesz zapisac nagrany plik? [t/n]: ");
        String saveYourRecord = scanner.next();
        if (saveYourRecord.equals("t")) {
            Sequence sequence = sequencer.getSequence();
            String fileName;
            System.out.print("Jaka chcesz nadac nazwe pliku?: ");
            fileName = scanner.next();
            String fileNameString = "C:\\Users\\" + user + "\\Desktop\\" + fileName + ".mid";
            File file = new File(fileNameString);
            MidiSystem.write(sequence, 0, file);
            System.out.println("Plik zostal zapisany na pulpicie pod nazwa: " + fileName);
        }
        myDevice.close();
        sequencer.close();
    }

    static Sequencer setSequencer(MidiDevice myDevice) throws MidiUnavailableException, InvalidMidiDataException {
        Sequencer sequencer = MidiSystem.getSequencer();
        Transmitter transmitter;
        Receiver receiver;
        myDevice.open();
        sequencer.open();
        transmitter = myDevice.getTransmitter();
        receiver = sequencer.getReceiver();
        transmitter.setReceiver(receiver);
        Sequence sequence = new Sequence(Sequence.PPQ, 24);
        Track track = sequence.createTrack();
        sequencer.setSequence(sequence);
        sequencer.setTickPosition(0);
        sequencer.recordEnable(track, -1);
        return sequencer;
    }

    public static ArrayList<String> displaySoundFromMidiFile(String user) throws InvalidMidiDataException, IOException {
        String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "H"};
        int note_on = 0x90;
        Scanner scanner  = new Scanner(System.in);
        System.out.print("Podaj nazwe pliku .midi z pulpitu, ktory chcesz zobrazowac: ");
        String fileName = scanner.next();
        String fileNameString = "C:\\Users\\" + user + "\\Desktop\\" + fileName + ".mid";
        File file = new File(fileNameString);
        Sequence sequence = MidiSystem.getSequence(file);
        Track[] track = sequence.getTracks();
        ArrayList<String> listOfNotes = new ArrayList<>();
        System.out.println();
        for (int j = 0; j < track[0].size(); j++) {
            MidiEvent event = track[0].get(j);
            MidiMessage message = event.getMessage();
            if (message instanceof ShortMessage sm) {
                if (sm.getCommand() == note_on) {
                    int key = sm.getData1();
                    int octave = (key / 12) - 1;
                    int note = key % 12;
                    String noteName = notes[note];
                    int velocity = sm.getData2();
                    if (velocity != 0) {
                        listOfNotes.add(noteName + octave);
                    }
                }
            }
        }
        System.out.println("Dzwieki wystepujace w tym pliku:");
        System.out.println(listOfNotes);
        return listOfNotes;
    }

    public static void compareMidiFiles(String user) throws InvalidMidiDataException, IOException {
        System.out.println("\nPLIK .MIDI WYSLANY PRZEZ NAUCZYCIELA");
        ArrayList<String> firstFile = displaySoundFromMidiFile(user);
        System.out.println("\nPLIK .MIDI NAGRANY PRZEZ CIEBIE");
        ArrayList<String> secondtFile = displaySoundFromMidiFile(user);

        if ((firstFile.size() != secondtFile.size()) || !checkListsAreTheSame(firstFile, secondtFile)) {
            System.out.println("\n\nNiestety nie zagrales tych samych dzwiekow co nauczyciel\n" +
                    firstFile +"  <- To sa dzwieki nauczyciela\n" +
                    secondtFile + "  <- To sa dzwieki nagrane przez ciebie\n" +
                    "Nagraj utwor jeszcze raz poprawnie");
        } else {
            System.out.println("\nGratuluje. Zagrales te same dzwieki co nauczyciel");
        }
    }

    public static String setUserName() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj swoja nazwe uzytkownika komputera: ");
        return scanner.next();
    }

    static boolean checkListsAreTheSame (ArrayList<String> list1, ArrayList<String> list2) {
        return list1.equals(list2);
    }
}