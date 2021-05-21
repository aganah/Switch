package umn.ac.id.aswitch;

public class TransHandler {
    private String jenis, rId, penerima, tanggal;
    private Double nominal;

    public TransHandler() {}


    public TransHandler(String jenis, String rId, String penerima, Double nominal, String tanggal) {
        this.jenis = jenis;
        this.rId = rId;
        this.penerima = penerima;
        this.nominal = nominal;
        this.tanggal = tanggal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getPenerima() {
        return penerima;
    }

    public void setPenerima(String penerima) {
        this.penerima = penerima;
    }

    public Double getNominal() {
        return nominal;
    }

    public void setNominal(Double nominal) {
        this.nominal = nominal;
    }
}
