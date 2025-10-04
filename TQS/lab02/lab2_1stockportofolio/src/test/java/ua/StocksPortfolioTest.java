package ua;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StocksPortfolioTest {
    
    @Mock
    private IStockmarketService stockmarketService;
    
    private StocksPortfolio portfolio;
    
    @BeforeEach
    void setUp() {
        portfolio = new StocksPortfolio(stockmarketService);
    }
    
    @Test
    void testTotalValue() {
        // Given - mock the stock prices to have predictable results
        when(stockmarketService.lookUpPrice("AAPL")).thenReturn(150.0);
        when(stockmarketService.lookUpPrice("GOOGL")).thenReturn(2500.0);
        when(stockmarketService.lookUpPrice("TSLA")).thenReturn(800.0);
        when(stockmarketService.lookUpPrice("MSFT")).thenReturn(300.0);
       // when(stockmarketService.lookUpPrice("AMZN")).thenReturn(3200.0);
        
        // When - add stocks to portfolio
        Stock apple = new Stock("AAPL", 10);
        Stock google = new Stock("GOOGL", 5);
        Stock tesla = new Stock("TSLA", 3);
        Stock microsoft = new Stock("MSFT", 20);
       // Stock amazon = new Stock("AMZN", 2);
        
        portfolio.addStock(apple);
        portfolio.addStock(google);
        portfolio.addStock(tesla);
        portfolio.addStock(microsoft);
       // portfolio.addStock(amazon);
        
        // Then - verify total value calculation
        // (150*10) + (2500*5) + (800*3) + (300*20) + (3200*2) = 1500 + 12500 + 2400 + 6000 + 6400 = 28800
        assertThat(portfolio.totalValue(), is(22400.0));
        
        // Verify that the service was called for each stock
        verify(stockmarketService).lookUpPrice("AAPL");
        verify(stockmarketService).lookUpPrice("GOOGL");
        verify(stockmarketService).lookUpPrice("TSLA");
        verify(stockmarketService).lookUpPrice("MSFT");
       // verify(stockmarketService).lookUpPrice("AMZN");
    }
    
    @Test
    void testEmptyPortfolio() {
        // Empty portfolio should have zero value
        assertThat(portfolio.totalValue(), is(0.0));
        
        // No calls to stock service should be made
        verifyNoInteractions(stockmarketService);
    }
    
    @Test
    void testAddStock() {
        Stock stock = new Stock("TSLA", 20);
        portfolio.addStock(stock);
        
        assertThat(portfolio.getStocks(), hasSize(1));
        assertThat(portfolio.getStocks().get(0).getLabel(), is("TSLA"));
        assertThat(portfolio.getStocks().get(0).getQuantity(), is(20));
    }
    
    @Test
    void testMostValuableStocks() {
        // Given - mock stock prices
        when(stockmarketService.lookUpPrice("AAPL")).thenReturn(150.0);  // 150 * 10 = 1500
        when(stockmarketService.lookUpPrice("GOOGL")).thenReturn(2500.0); // 2500 * 5 = 12500
        when(stockmarketService.lookUpPrice("TSLA")).thenReturn(800.0);   // 800 * 3 = 2400
        
        // When - add stocks
        portfolio.addStock(new Stock("AAPL", 10));   // value: 1500
        portfolio.addStock(new Stock("GOOGL", 5));   // value: 12500 (highest)
        portfolio.addStock(new Stock("TSLA", 3));    // value: 2400
        
        // Then - get top 2 most valuable
        List<Stock> top2 = portfolio.mostValuableStocks(2);
        
        assertThat(top2, hasSize(2));
        assertThat(top2.get(0).getLabel(), is("GOOGL")); // First: GOOGL (12500)
        assertThat(top2.get(1).getLabel(), is("TSLA"));  // Second: TSLA (2400)
    }
    
    @Test
    void testMostValuableStocksEdgeCases() {
        // Given - portfolio with 2 stocks
        when(stockmarketService.lookUpPrice("AAPL")).thenReturn(100.0);
        when(stockmarketService.lookUpPrice("TSLA")).thenReturn(200.0);
        
        portfolio.addStock(new Stock("AAPL", 1)); // value: 100
        portfolio.addStock(new Stock("TSLA", 1)); // value: 200
        
        // Edge case: topN = 0
        assertThat(portfolio.mostValuableStocks(0), hasSize(0));
        
        // Edge case: topN > available stocks
        List<Stock> all = portfolio.mostValuableStocks(5);
        assertThat(all, hasSize(2));
        assertThat(all.get(0).getLabel(), is("TSLA"));
        
        // Edge case: topN = exact number of stocks
        List<Stock> exact = portfolio.mostValuableStocks(2);
        assertThat(exact, hasSize(2));
    }
    
    @Test
    void testMostValuableStocksEmptyPortfolio() {
        // Edge case: empty portfolio
        List<Stock> empty = portfolio.mostValuableStocks(3);
        assertThat(empty, hasSize(0));
    }
}
