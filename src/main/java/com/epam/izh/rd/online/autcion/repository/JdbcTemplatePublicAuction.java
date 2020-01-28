package com.epam.izh.rd.online.autcion.repository;

import com.epam.izh.rd.online.autcion.entity.Bid;
import com.epam.izh.rd.online.autcion.entity.Item;
import com.epam.izh.rd.online.autcion.entity.User;
import com.epam.izh.rd.online.autcion.mappers.BidMapper;
import com.epam.izh.rd.online.autcion.mappers.ItemMapper;
import com.epam.izh.rd.online.autcion.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JdbcTemplatePublicAuction implements PublicAuction {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Bid> getUserBids(long id) {
        String query = "SELECT * FROM bids WHERE user_id = ?";
        return jdbcTemplate.query(query, bidMapper, id);
    }

    @Override
    public List<Item> getUserItems(long id) {
        String query = "SELECT * FROM items WHERE user_id = ?";
        return jdbcTemplate.query(query, itemMapper, id);
    }

    private List<Bid> getItemBids(long id) {
        String query = "SELECT * FROM bids WHERE item_id = ?";
        return jdbcTemplate.query(query, bidMapper, id);
    }

    @Override
    public Item getItemByName(String name) {
        String query = "SELECT * FROM items WHERE title LIKE '%" + name + "%' LIMIT 1";
        return jdbcTemplate.queryForObject(query, itemMapper);
    }

    @Override
    public Item getItemByDescription(String name) {
        String query = "SELECT * FROM items WHERE description LIKE '%" + name + "%' LIMIT 1";
        return jdbcTemplate.queryForObject(query, itemMapper);
    }

    @Override
    public Map<User, Double> getAvgItemCost() {
        Map<User, Double> avgItemCost = new HashMap<>();
        List<User> users = jdbcTemplate.query("SELECT * FROM users", userMapper);
        List<Item> items;
        double itemsCost;
        for (User user: users) {
            itemsCost = 0;
            items = getUserItems(user.getUserId());
            for (Item item: items) {
                itemsCost += item.getStartPrice();
            }
            if (items.size() != 0) {
                avgItemCost.put(user, itemsCost/items.size());
            }
        }
        return avgItemCost;
    }

    @Override
    public Map<Item, Bid> getMaxBidsForEveryItem() {
        Map<Item, Bid> maxBidsForEveryItem = new HashMap<>();
        List<Item> items = jdbcTemplate.query("SELECT * FROM items", itemMapper);
        List<Bid> bids;
        Bid maxBid;
        for (Item item: items) {
            maxBid = null;
            bids = getItemBids(item.getItemId());
            for (Bid bid: bids) {
                if (maxBid == null || bid.getBidValue() > maxBid.getBidValue()) {
                    maxBid = bid;
                }
                maxBidsForEveryItem.put(item, maxBid);
            }
        }
        return maxBidsForEveryItem;
    }

    @Override
    // без решения
    public List<Bid> getUserActualBids(long id) {
        return new ArrayList<>(Collections.singletonList(new Bid()));
    }

    @Override
    public boolean createUser(User user) {
        List<User> existUsers = jdbcTemplate.query("SELECT * FROM users", userMapper);
        for (User existsUser: existUsers) {
            if (existsUser.getUserId().equals(user.getUserId())) {
                return false;
            }
        }
        int updatedRowsCount = jdbcTemplate.update("INSERT INTO users VALUES (?,?,?,?,?)",
                user.getUserId(), user.getBillingAddress(), user.getFullName(), user.getLogin(), user.getPassword()
        );
        return updatedRowsCount != 0;
    }

    @Override
    public boolean createItem(Item item) {
        List<Item> existItems = jdbcTemplate.query("SELECT * FROM items", itemMapper);
        for (Item existsItem: existItems) {
            if (existsItem.getItemId().equals(item.getItemId())) {
                return false;
            }
        }
        int updatedRowsCount = jdbcTemplate.update("INSERT INTO items VALUES (?,?,?,?,?,?,?,?,?)",
                item.getItemId(), item.getBidIncrement(), item.getBuyItNow(),
                item.getDescription(), item.getStartDate(), item.getStartPrice(),
                item.getStopDate(), item.getTitle(), item.getUserId()
        );
        return updatedRowsCount != 0;
    }

    @Override
    public boolean createBid(Bid bid) {
        List<Bid> existBids = jdbcTemplate.query("SELECT * FROM bids",bidMapper);
        for (Bid existsBid: existBids) {
            if (existsBid.getBidId().equals(bid.getBidId())) {
                return false;
            }
        }
        int updatedRowsCount = jdbcTemplate.update("INSERT INTO bids VALUES (?,?,?,?,?)",
                bid.getBidId(), bid.getBidDate(), bid.getBidValue(), bid.getItemId(), bid.getUserId()
        );
        return updatedRowsCount != 0;
    }

    @Override
    public boolean deleteUserBids(long id) {
        int updatedRowsCount = jdbcTemplate.update("DELETE FROM bids WHERE user_id = ?", id);
        return updatedRowsCount != 0;
    }

    @Override
    public boolean doubleItemsStartPrice(long id) {
        int updatedRowsCount = jdbcTemplate.update(
                "UPDATE items SET start_price = start_price * 2 WHERE user_id = ?", id
        );
        return updatedRowsCount != 0;
    }
}
