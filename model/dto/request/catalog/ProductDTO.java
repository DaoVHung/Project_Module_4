package ra.dev.model.dto.request.catalog;import lombok.AllArgsConstructor;import lombok.Getter;import lombok.NoArgsConstructor;import lombok.Setter;@Getter@Setter@NoArgsConstructorpublic class ProductDTO {    private String productName;    private Boolean prodcutStatus;    public ProductDTO(String productName, Boolean prodcutStatus) {        this.productName = productName;        this.prodcutStatus = prodcutStatus;    }}