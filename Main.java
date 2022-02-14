
import java.io.*;
import java.text.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner terminalInput = new Scanner(System.in);
        String pilihanUser;
        boolean isLanjutkan = true;

        while (isLanjutkan) {
            clearScreen();
            System.out.println("Database Mahasiswa\n");
            System.out.println("1.\tLihat seluruh Mahasiswa");
            System.out.println("2.\tCari data Mahasiswa");
            System.out.println("3.\tTambah data Mahasiswa");
            System.out.println("4.\tUbah data Mahasiswa");
            System.out.println("5.\tHapus data Mahasiswa");
            System.out.println("6.\tKeluar");

            System.out.print("\n\nPilihan anda: ");
            pilihanUser = terminalInput.next();

            switch (pilihanUser) {
                case "1":
                    System.out.println("\n=========================");
                    System.out.println("LIST SELURUH DATA MAHASISWA");
                    System.out.println("===========================");
                    tampilkanData();
                    break;
                case "2":
                    System.out.println("\n=================");
                    System.out.println("CARI DATA MAHASISWA");
                    System.out.println("===================");
                    cariData();
                    break;
                case "3":
                    System.out.println("\n===================");
                    System.out.println("TAMBAH DATA MAHASISWA");
                    System.out.println("=====================");
                    tambahData();
                    tampilkanData();
                    break;
                case "4":
                    System.out.println("\n=================");
                    System.out.println("UBAH DATA MAHASISWA");
                    System.out.println("===================");
                    updateData();
                    break;
                case "5":
                    System.out.println("\n==================");
                    System.out.println("HAPUS DATA MAHASISWA");
                    System.out.println("====================");
                    deleteData();
                    break;
                case "6":
                    System.out.println("Terimakasih! \uD83D\uDE00");
                    System.exit(0);
                default:
                    System.err.println("\nInput anda tidak ditemukan\nSilahkan pilih [1-5]");
            }
            isLanjutkan = getYesorNo("Apakah Anda ingin melanjutkan");
        }
        System.out.println("Terimakasih! \uD83D\uDE00");
    }

    private static void updateData() throws IOException {
        // kita ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // kita buat database sementara
        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // tampilkan data
        System.out.println("List Data Mahasiswa");
        tampilkanData();

        // ambil user input / pilihan data
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\nMasukan nomor mahasiswa yang akan diupdate: ");
        int updateNum = terminalInput.nextInt();

        // tampilkan data yang ingin diupdate

        String data = bufferedInput.readLine();
        int entryCounts = 0;

        while (data != null) {
            entryCounts++;

            StringTokenizer st = new StringTokenizer(data, ",");

            // tampilkan entrycounts == updateNum
            if (updateNum == entryCounts) {
                System.out.println("\nData yang ingin anda update adalah:");
                System.out.println("---------------------------------------");
                System.out.println("Nama Mahasiswa : " + st.nextToken());
                System.out.println("Nim            : " + st.nextToken());
                System.out.println("Tanggal Lahir  : " + st.nextToken());

                // update data

                // mengambil input dari user

                String[] fieldData = { "Nama Mahasiswa", "Nim", "Tanggal Lahir" };
                String[] tempData = new String[3];

                st = new StringTokenizer(data, ",");
                String originalData = st.nextToken();

                for (int i = 0; i < fieldData.length; i++) {
                    boolean isUpdate = getYesorNo("apakah anda ingin merubah " + fieldData[i]);
                    // originalData = st.nextToken();
                    if (isUpdate) {
                        // user input
                        if (fieldData[i].equalsIgnoreCase("Tanggal Lahir")) {
                            System.out.print("masukan Tanggal Lahir baru format DD-MM-YYYY: ");
                            tempData[i] = terminalInput.nextLine();
                            while (!isTanggalLahir(tempData[i])) {
                                System.err.println("tanggal lahir tidak valid!");
                                System.out.print("masukan tanggal lahir format DD-MM-YYYY: ");
                                tempData[i] = terminalInput.nextLine();
                            }
                        } else {
                            terminalInput = new Scanner(System.in);
                            System.out.print("\nMasukan " + fieldData[i] + " baru: ");
                            tempData[i] = terminalInput.nextLine();
                        }

                    } else {
                        originalData = st.nextToken();
                        tempData[i] = originalData;
                    }
                }

                // tampilkan data baru ke layar
                st = new StringTokenizer(data, ",");
                System.out.println("\nData baru anda adalah ");
                System.out.println("---------------------------------------");
                System.out.println("nama mahasiswa : " + st.nextToken() + " --> " + tempData[0]);
                System.out.println("nim            : " + st.nextToken() + " --> " + tempData[1]);
                System.out.println("tanggal lahir  : " + st.nextToken() + " --> " + tempData[2]);

                boolean isUpdate = getYesorNo("apakah anda yakin ingin mengupdate data tersebut");

                if (isUpdate) {
                    // cek data baru di database
                    boolean isExist = cekMahasiswaDiDatabase(tempData, false);

                    if (isExist) {
                        System.err.println(
                                "data mahasiswa sudah ada di database, proses update dibatalkan, \nsilahkan delete data yang bersangkutan");
                        // copy data
                        bufferedOutput.write(data);

                    } else {

                        // format data baru kedalam database
                        String namaMahasiswa = tempData[0];
                        String nim = tempData[1];
                        String tanggalLahir = tempData[2];

                        // tulis data ke database
                        bufferedOutput.write(namaMahasiswa + "," + nim + "," + tanggalLahir);
                    }
                } else {
                    // copy data
                    bufferedOutput.write(data);
                }
            } else {
                // copy data
                bufferedOutput.write(data);
            }
            bufferedOutput.newLine();

            data = bufferedInput.readLine();
        }
        // menulis data ke file
        bufferedOutput.flush();

        // delete original database
        database.delete();
        // rename file tempDB menjadi database
        tempDB.renameTo(database);
        tampilkanData();
    }

    private static void deleteData() throws IOException {
        // kita ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // kita buat database sementara
        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // tampilkan data
        System.out.println("List Data Mahasiswa");
        tampilkanData();

        // kita ambil user input untuk mendelete data
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\nMasukan nomor mahasiswa yang akan dihapus: ");
        int deleteNum = terminalInput.nextInt();

        // looping untuk membaca tiap data baris dan skip data yang akan didelete

        boolean isFound = false;
        int entryCounts = 0;

        String data = bufferedInput.readLine();

        while (data != null) {
            entryCounts++;
            boolean isDelete = false;

            StringTokenizer st = new StringTokenizer(data, ",");

            // tampilkan data yang ingin di hapus
            if (deleteNum == entryCounts) {
                System.out.println("\nData yang ingin anda hapus adalah:");
                System.out.println("---------------------------------------");
                System.out.println("Nama Mahasiswa : " + st.nextToken());
                System.out.println("Nim            : " + st.nextToken());
                System.out.println("Tanggal Lahir  : " + st.nextToken());

                isDelete = getYesorNo("Apakah anda yakin akan menghapus?");
                isFound = true;
            }

            if (isDelete) {
                // skip pindahkan data dari original ke sementara
                System.out.println("Data berhasil dihapus");
            } else {
                // kita pindahkan data dari original ke sementara
                bufferedOutput.write(data);
                bufferedOutput.newLine();
            }
            data = bufferedInput.readLine();
        }

        if (!isFound) {
            System.err.println("Data Mahasiswa tidak ditemukan");
        }

        // menulis data ke file
        bufferedOutput.flush();
        // delete original file
        database.delete();
        // rename file sementara ke database
        tempDB.renameTo(database);

        tampilkanData();
    }

    private static void tampilkanData() throws IOException {

        FileReader fileInput;
        BufferedReader bufferInput;

        try {
            fileInput = new FileReader("database.txt");
            bufferInput = new BufferedReader(fileInput);
        } catch (Exception e) {
            System.err.println("Database Tidak ditemukan");
            System.err.println("Silahkan tambah data terlebih dahulu");
            tambahData();
            return;
        }

        System.out.println("\n| No |\t\tNama Mahasiswa\t\t|     Nim     | Tanggal Lahir");
        System.out.println("-----------------------------------------------------------------------");

        String data = bufferInput.readLine();
        int nomorData = 0;
        while (data != null) {
            nomorData++;

            StringTokenizer stringToken = new StringTokenizer(data, ",");

            System.out.printf("| %2d ", nomorData);
            System.out.printf("| %-33s", stringToken.nextToken());
            System.out.printf("| %-12s", stringToken.nextToken());
            System.out.printf("| %-10s", stringToken.nextToken());
            // System.out.printf("|\t%s ", stringToken.nextToken());
            System.out.print("\n");

            data = bufferInput.readLine();
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    private static void cariData() throws IOException {

        // membaca database ada atau tidak

        try {
            File file = new File("database.txt");
        } catch (Exception e) {
            System.err.println("Database Tidak ditemukan");
            System.err.println("Silahkan tambah data terlebih dahulu");
            tambahData();
            return;
        }

        // kita ambil keyword dari user

        Scanner terminalInput = new Scanner(System.in);
        System.out.print("Masukan kata kunci untuk mencari data mahasiswa: ");
        String cariString = terminalInput.nextLine();
        System.out.println(cariString);
        String[] keywords = cariString.split("\\s+");

        // kita cek keyword di database
        cekMahasiswaDiDatabase(keywords, true);

    }

    private static void tambahData() throws IOException {

        FileWriter fileOutput = new FileWriter("database.txt", true);
        BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

        // mengambil input dari user
        Scanner terminalInput = new Scanner(System.in);
        String namaMahasiswa, nim, tanggalLahir;

        System.out.print("masukan nama mahasiswa: ");
        namaMahasiswa = terminalInput.nextLine();
        System.out.print("masukan nim: ");
        nim = terminalInput.nextLine();
        System.out.print("masukan tanggal lahir format DD-MM-YYYY: ");
        tanggalLahir = terminalInput.nextLine();

        // jika tanggal lahir tidak valid
        while (!isTanggalLahir(tanggalLahir)) {
            System.err.println("tanggal lahir tidak valid!");
            System.out.print("masukan tanggal lahir format DD-MM-YYYY: ");
            tanggalLahir = terminalInput.nextLine();
        }

        // cek mahasiswa di database

        String[] keywords = { namaMahasiswa + "," + nim + "," + tanggalLahir };
        System.out.println(Arrays.toString(keywords));

        boolean isExist = cekMahasiswaDiDatabase(keywords, false);

        // menulis mahasiswa di databse
        if (!isExist) {
            System.out.println("\nData yang akan anda masukan adalah");
            System.out.println("----------------------------------------");
            System.out.println("Nama Mahasiswa : " + namaMahasiswa);
            System.out.println("nim            : " + nim);
            System.out.println("tanggal lahir  : " + tanggalLahir);

            boolean isTambah = getYesorNo("Apakah akan ingin menambah data tersebut? ");

            if (isTambah) {
                bufferOutput.write(namaMahasiswa + "," + nim + "," + tanggalLahir);
                bufferOutput.newLine();
                bufferOutput.flush();
            }

        } else {
            System.out.println("data mahasiswa yang anda akan masukan sudah tersedia di database dengan data berikut:");
            cekMahasiswaDiDatabase(keywords, true);
        }
        bufferOutput.close();
    }

    private static boolean cekMahasiswaDiDatabase(String[] keywords, boolean isDisplay) throws IOException {

        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        String data = bufferInput.readLine();
        boolean isExist = false;
        int nomorData = 0;

        if (isDisplay) {
            System.out.println("\n| No |\t\tNama Mahasiswa\t\t|     Nim     | Tanggal Lahir");
            System.out.println("-----------------------------------------------------------------------");
        }

        while (data != null) {

            // cek keywords didalam baris
            isExist = true;

            for (String keyword : keywords) {
                isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
            }

            // jika keywordsnya cocok maka tampilkan

            if (isExist) {
                if (isDisplay) {
                    nomorData++;
                    StringTokenizer stringToken = new StringTokenizer(data, ",");

                    System.out.printf("| %2d ", nomorData);
                    System.out.printf("| %-33s", stringToken.nextToken());
                    System.out.printf("| %-12s", stringToken.nextToken());
                    System.out.printf("| %-10s", stringToken.nextToken());
                    // System.out.printf("|\t%s ", stringToken.nextToken());
                    System.out.print("\n");
                } else {
                    break;
                }
            }

            data = bufferInput.readLine();
        }

        if (isDisplay) {
            System.out.println("-----------------------------------------------------------------------");
        }

        return isExist;
    }

    private static boolean getYesorNo(String message) {
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\n" + message + " (y/n)? ");
        String pilihanUser = terminalInput.next();

        while (!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")) {
            System.err.println("Pilihan anda bukan y atau n");
            System.out.print("\n" + message + " (y/n)? ");
            pilihanUser = terminalInput.next();
        }

        return pilihanUser.equalsIgnoreCase("y");

    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (Exception ex) {
            System.err.println("tidak bisa clear screen");
        }
    }

    private static boolean isTanggalLahir(String tgl) {
        // Pola tgl(2 digit)/bulan(2 digit)/tahun(4 digit)
        String pola = "dd-MM-yyyy";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pola);
            // Tidak ada toleransi interpretasi
            sdf.setLenient(false);
            // Parsing masukan tgl berdasar pola
            sdf.parse(tgl);
            // Jika berhasil merepresentasikan Date, berarti ok
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
}
