package pl.asiedlecki.model;

public class Samochod {
    private String rodzaj;
    private String marka;
    private String model;
    private int rok_produkcji;

    public Samochod() {
    }

    public Samochod(String rodzaj, String marka, String model, int rok_produkcji) {
        this.rodzaj = rodzaj;
        this.marka = marka;
        this.model = model;
        this.rok_produkcji = rok_produkcji;
    }

    public String getRodzaj() {
        return rodzaj;
    }

    public void setRodzaj(String rodzaj) {
        this.rodzaj = rodzaj;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getRok_produkcji() {
        return rok_produkcji;
    }

    public void setRok_produkcji(int rok_produkcji) {
        this.rok_produkcji = rok_produkcji;
    }
}
