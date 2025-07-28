
import java.util.List;
import java.util.Objects;

public class Cadastral_Object {
    static private int all_id = 100;
    private int id;
    private int price; //Цена
    private String currency; //Тип валюты
    private String owner; //Владелец
    private List<String> sellers; //Бывшие владельцы
    private String purchase_date; //Дата покупки
    private String status; //Статус (продаётся, купленно, стройка, заброшено, бронь, готово)

    private String picture; //Картинка владельца

    public Cadastral_Object(int id, int price, String currency,
                            String owner, List<String> sellers,
                            String purchase_date, String status,String picture) {

        this.id = id;
        this.price = price;
        this.currency = currency;
        this.owner = owner;
        this.sellers = sellers;
        this.purchase_date = purchase_date;
        this.status = status;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getSellers() {
        return sellers;
    }

    public void setSellers(List<String> sellers) {
        this.sellers = sellers;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cadastral_Object that = (Cadastral_Object) o;
        return id == that.id && price == that.price && Objects.equals(currency, that.currency) && Objects.equals(owner, that.owner) && Objects.equals(sellers, that.sellers) && Objects.equals(purchase_date, that.purchase_date) && Objects.equals(status, that.status) && Objects.equals(picture, that.picture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, currency, owner, sellers, purchase_date, status, picture);
    }

    void addSeller(String seller){
        this.sellers.add(seller);
    }
}
