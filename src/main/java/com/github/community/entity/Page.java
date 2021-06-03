package com.github.community.entity;

/**
 * 分页相关
 */
public class Page {
    // 当前页码
    private int currentPage = 1;
    // 一页最多能显示多少条数据
    private int limit = 10;
    // discussPost 数据总数； 用于计算总的页数，使用 rows / limit 即可得到共有多少页
    private int rows;
    // 查询路径 用来复用分页的链接
    private String path;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage >= 1) {
            this.currentPage = currentPage;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 获取当前页 discussPost 的起始项是从第几条数据开始的
    public int getOffset() {
        return (currentPage - 1) * limit;
    }

    // 获取总的页数
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    // getFrom getTo 方法为 从第几页到到第几页
    // 当前页码的前两页以及后两页

    /**
     * @return 起始页码
     */

    public int getFrom() {
        int from = currentPage - 2;
        return Math.max(from, 1);
    }

    /**
     * @return 结束页码
     */
    public int getTo() {
        int total = getTotal();
        int to = currentPage + 2;
        return Math.min(to, total);
    }

}
