// Manual integration test — run directly from IDE, not via ./gradlew test
package com.biniyogbuddy.scraper;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

public class ScraperTest {

    private static final String PRICE_URL  = "https://dsebd.org/latest_share_price_scroll_l.php";
    private static final String DSEX_URL   = "https://dsebd.org/dseX_share.php";
    private static final String INDEX_URL  = "https://dsebd.org/index.php";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36";
    private static final int    TIMEOUT_MS = 25_000;

    public static void main(String[] args) {
        System.out.println("=== DSE Scraper Test ===\n");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            // Test 1: stock prices (already confirmed working)
            testStockPrices(browser);
            System.out.println();

            // Test 2: index values from dseX_share.php
            testIndexPage(browser);
            System.out.println();

            // Test 3: market stats from index.php
            testMarketStats(browser);

            browser.close();
        } catch (Exception e) {
            System.err.println("FATAL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── TEST 1: Stock prices ──────────────────────────────────────────────────

    private static void testStockPrices(Browser browser) {
        System.out.println("TEST 1 — Stock prices (latest_share_price_scroll_l.php)");
        System.out.println("---------------------------------------------------------");
        Document doc = fetchPage(browser, PRICE_URL);

        int valid = 0;
        String firstCode = null, firstLtp = null;
        for (Element row : doc.select("table.table tbody tr")) {
            Elements cells = row.select("td");
            if (cells.size() < 11) continue;
            String code = cells.get(1).text().trim();
            if (code.isEmpty() || code.equalsIgnoreCase("TRADING CODE")) continue;
            valid++;
            if (firstCode == null) { firstCode = code; firstLtp = cells.get(2).text().trim(); }
        }

        System.out.println("Valid rows   : " + valid);
        System.out.println("First stock  : " + firstCode + " → LTP = " + firstLtp);
        System.out.println("Market status: " + detectMarketStatus(doc));
        System.out.println("RESULT       : " + (valid > 0 ? "✓ PASS" : "✗ FAIL"));

        // ── Bonus: does this page also carry the DSEX index value? ───────────
        System.out.println("\n--- Scanning price page for DSEX/DSES/DS30 elements ---");
        for (Element el : doc.select("*")) {
            String own = el.ownText().trim();
            if (own.isEmpty()) continue;
            if ((own.contains("DSEX") || own.contains("DSES") || own.contains("DS30"))
                    && own.matches(".*\\d+.*")) {
                System.out.println("  <" + el.tagName() + "> class='" + el.className() + "' id='" + el.id() + "' : " + own);
            }
        }
        System.out.println("--- Tables on price page containing DSEX/DSES/DS30 ---");
        int tn = 0;
        for (Element table : doc.select("table")) {
            tn++;
            if (!table.text().contains("DSEX") && !table.text().contains("DSES") && !table.text().contains("DS30")) continue;
            Elements rows = table.select("tr");
            System.out.println("\n  Table #" + tn + " class='" + table.className() + "' rows=" + rows.size());
            int printed = 0;
            for (Element row : rows) {
                Elements cells = row.select("td, th");
                if (cells.isEmpty()) continue;
                StringBuilder sb = new StringBuilder("    |");
                for (Element c : cells) sb.append(" ").append(c.text().trim()).append(" |");
                System.out.println(sb);
                if (++printed >= 10) { System.out.println("    ... (truncated)"); break; }
            }
        }
    }

    // ── TEST 2: Index values ──────────────────────────────────────────────────

    private static void testIndexPage(Browser browser) {
        System.out.println("TEST 2 — Index values (dseX_share.php)");
        System.out.println("----------------------------------------");
        Document doc = fetchPage(browser, DSEX_URL);

        System.out.println("Total tables: " + doc.select("table").size());

        // ── 2a: Dump every TABLE that mentions DSEX/DSES/DS30 (any size) ─────
        System.out.println("\n--- Tables containing DSEX/DSES/DS30 ---");
        boolean foundIndex = false;
        int n = 0;
        for (Element table : doc.select("table")) {
            n++;
            String tableText = table.text();
            boolean mentionsIndex = tableText.contains("DSEX") || tableText.contains("DSES") || tableText.contains("DS30");
            if (!mentionsIndex) continue;

            Elements rows = table.select("tr");
            System.out.println("\n  Table #" + n + " class='" + table.className() + "' rows=" + rows.size());
            int printed = 0;
            for (Element row : rows) {
                Elements cells = row.select("td, th");
                if (cells.isEmpty()) continue;
                StringBuilder sb = new StringBuilder("    |");
                for (Element c : cells) sb.append(" ").append(c.text().trim()).append(" |");
                System.out.println(sb);
                if (++printed >= 10) { System.out.println("    ... (truncated)"); break; }
            }

            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.size() < 4) continue;
                String name = cells.get(0).text().trim();
                if (name.contains("DSEX") || name.contains("DSES") || name.contains("DS30")) {
                    System.out.println("\n  *** INDEX ROW FOUND: ***");
                    System.out.printf("    %-6s  value=%-14s  change=%-12s  pct=%s%n",
                            name, cells.get(1).text().trim(),
                            cells.get(2).text().trim(), cells.get(3).text().trim());
                    foundIndex = true;
                }
            }
        }

        // ── 2b: Scan non-table elements for DSEX numeric values ──────────────
        System.out.println("\n--- Non-table elements containing DSEX + a number ---");
        for (Element el : doc.select("*")) {
            String own = el.ownText().trim();
            if (own.isEmpty()) continue;
            if ((own.contains("DSEX") || own.contains("DSES") || own.contains("DS30"))
                    && own.matches(".*\\d+.*")) {
                System.out.println("  <" + el.tagName() + "> class='" + el.className() + "' : " + own);
            }
        }

        // ── 2c: Dump Table #1 always (nav/header often skipped but may hide data) ──
        Element firstTable = doc.selectFirst("table");
        if (firstTable != null) {
            System.out.println("\n--- Table #1 (class='" + firstTable.className() + "', rows=" + firstTable.select("tr").size() + ") ---");
            int printed = 0;
            for (Element row : firstTable.select("tr")) {
                Elements cells = row.select("td, th");
                if (cells.isEmpty()) continue;
                StringBuilder sb = new StringBuilder("  |");
                for (Element c : cells) sb.append(" ").append(c.text().trim()).append(" |");
                System.out.println(sb);
                if (++printed >= 15) { System.out.println("  ... (truncated)"); break; }
            }
        }

        System.out.println("\nRESULT: " + (foundIndex ? "✓ PASS" : "✗ FAIL — check sections 2a/2b/2c above"));
    }

    // ── TEST 3: Market stats ──────────────────────────────────────────────────

    private static void testMarketStats(Browser browser) {
        System.out.println("TEST 3 — Market stats (index.php)");
        System.out.println("-----------------------------------");
        Document doc = fetchPage(browser, INDEX_URL);

        boolean found = false;
        for (Element table : doc.select("table")) {
            if (!table.text().contains("Total Trade")) continue;

            Elements rows = table.select("tr");
            System.out.println("Stats table found — all rows:");

            for (Element row : rows) {
                Elements cells = row.select("td, th");
                if (cells.isEmpty()) continue;
                StringBuilder sb = new StringBuilder("  |");
                for (Element c : cells) sb.append(" ").append(c.text().trim()).append(" |");
                System.out.println(sb);
            }

            // Parse using header-based column detection
            if (rows.size() >= 2) {
                Elements header = rows.get(0).select("td, th");
                Elements data   = rows.get(1).select("td, th");

                int tradeCol = 1, volumeCol = 2, valueCol = 3;
                for (int i = 0; i < header.size(); i++) {
                    String h = header.get(i).text();
                    if (h.contains("Total Trade") && !h.contains("Volume")) tradeCol  = i;
                    if (h.contains("Total Volume"))  volumeCol = i;
                    if (h.contains("Total Value"))   valueCol  = i;
                }

                System.out.println("\nParsed values:");
                System.out.println("  Date         : " + data.get(0).text().trim());
                System.out.println("  Total trades : " + (tradeCol  < data.size() ? data.get(tradeCol).text().trim()  : "?"));
                System.out.println("  Total volume : " + (volumeCol < data.size() ? data.get(volumeCol).text().trim() : "?"));
                System.out.println("  Total value  : " + (valueCol  < data.size() ? data.get(valueCol).text().trim()  : "?") + " mn BDT");
                found = true;
            }
            break;
        }

        System.out.println("RESULT: " + (found ? "✓ PASS" : "✗ FAIL"));

        // ── Bonus: scan index.php for DSEX/DSES/DS30 values ─────────────────
        System.out.println("\n--- Scanning index.php for index values (non-table elements) ---");
        for (Element el : doc.select("*")) {
            String own = el.ownText().trim();
            if (own.isEmpty()) continue;
            if (own.contains("DSEX") || own.contains("DSES") || own.contains("DS30")) {
                System.out.println("  <" + el.tagName() + "> class='" + el.className() + "' id='" + el.id() + "' : " + own);
            }
        }

        System.out.println("\n--- Tables on index.php containing DSEX/DSES/DS30 ---");
        int n = 0;
        for (Element table : doc.select("table")) {
            n++;
            if (!table.text().contains("DSEX") && !table.text().contains("DSES") && !table.text().contains("DS30")) continue;
            Elements rows = table.select("tr");
            System.out.println("\n  Table #" + n + " class='" + table.className() + "' rows=" + rows.size());
            int printed = 0;
            for (Element row : rows) {
                Elements cells = row.select("td, th");
                if (cells.isEmpty()) continue;
                StringBuilder sb = new StringBuilder("    |");
                for (Element c : cells) sb.append(" ").append(c.text().trim()).append(" |");
                System.out.println(sb);
                if (++printed >= 10) { System.out.println("    ... (truncated)"); break; }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Document fetchPage(Browser browser, String url) {
        System.out.println("Fetching: " + url);
        long start = System.currentTimeMillis();
        Page page = browser.newPage();
        page.setExtraHTTPHeaders(Map.of("User-Agent", USER_AGENT));
        page.navigate(url);
        page.waitForLoadState(
                com.microsoft.playwright.options.LoadState.NETWORKIDLE,
                new Page.WaitForLoadStateOptions().setTimeout(TIMEOUT_MS)
        );
        String html = page.content();
        page.close();
        System.out.printf("Done in %dms%n", System.currentTimeMillis() - start);
        return Jsoup.parse(html);
    }

    private static String detectMarketStatus(Document doc) {
        String t = doc.text().toLowerCase();
        if (t.contains("market status: open")) return "OPEN";
        if (t.contains("pre-open"))            return "PRE_OPEN";
        return "CLOSED";
    }
}