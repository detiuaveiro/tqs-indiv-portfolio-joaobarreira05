package ua;

import java.util.ArrayList;
import java.util.List;

public class StocksPortfolio {
    private IStockmarketService stockmarket;
    private List<Stock> stocks;
    
    public StocksPortfolio(IStockmarketService stockmarket) {
        this.stockmarket = stockmarket;
        this.stocks = new ArrayList<>();
    }
    
    public void addStock(Stock stock) {
        stocks.add(stock);
    }
    
    public double totalValue() {
        double total = 0.0;
        for (Stock stock : stocks) {
            double currentPrice = stockmarket.lookUpPrice(stock.getLabel());
            total += currentPrice * stock.getQuantity();
        }
        return total;
    }
    
    /**
     * @param topN the number of most valuable stocks to return
     * @return a list with the topN most valuable stocks in the portfolio
     */
    public List<Stock> mostValuableStocks(int topN) {
        return stocks.stream()
            .sorted((s1, s2) -> {
                double value1 = stockmarket.lookUpPrice(s1.getLabel()) * s1.getQuantity();
                double value2 = stockmarket.lookUpPrice(s2.getLabel()) * s2.getQuantity();
                return Double.compare(value2, value1); // Descending order
            })
            .limit(topN)
            .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Stock> getStocks() {
        return stocks;
    }
}
