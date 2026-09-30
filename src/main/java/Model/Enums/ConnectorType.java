package Model.Enums;

public enum ConnectorType {
    SINGLE(1),
    DOUBLE(2),
    UNIVERSAL(3),
    NONE(0);

    private final int numero;

    ConnectorType(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return numero;
    }
}
