package pl.pregiel.dice_app.dtos;


public class RollValueDto {
    private int maxValue;
    private int value;

    public RollValueDto() {
    }

    public RollValueDto(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
