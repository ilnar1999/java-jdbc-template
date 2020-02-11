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

    private List<Bid> bids;
    private List<Item> items;
    private List<User> users;
    private Map<User, Double> avgItemCost;
    private Map<Item, Bid> maxBidsForEveryItem;

    private int updatedRowsCount;

    @Override
    public List<Bid> getUserBids(long id) {
        return jdbcTemplate.query("SELECT * FROM bids WHERE user_id = ?", bidMapper, id);
    }

    @Override
    public List<Item> getUserItems(long id) {
        return jdbcTemplate.query("SELECT * FROM items WHERE user_id = ?", itemMapper, id);
    }

    private List<Bid> getItemBids(long id) {
        return jdbcTemplate.query("SELECT * FROM bids WHERE item_id = ?", bidMapper, id);
    }

    @Override
    public Item getItemByName(String name) {
        return jdbcTemplate.queryForObject("SELECT * FROM items WHERE title LIKE '%" + name + "%' LIMIT 1", itemMapper);
    }

    @Override
    public Item getItemByDescription(String name) {
        return jdbcTemplate.queryForObject("SELECT * FROM items WHERE description LIKE '%" + name + "%' LIMIT 1", itemMapper);
    }

    @Override
    public Map<User, Double> getAvgItemCost() {
        avgItemCost = new HashMap<>();
        users = jdbcTemplate.query("SELECT * FROM users", userMapper);
        double itemsCost;
        for (User user : users) {
            itemsCost = 0;
            items = getUserItems(user.getUserId());
            for (Item item : items) {
                itemsCost += item.getStartPrice();
            }
            if (items.size() != 0) {
                avgItemCost.put(user, itemsCost / items.size());
            }
        }
        return avgItemCost;
    }

    @Override
    public Map<Item, Bid> getMaxBidsForEveryItem() {
        maxBidsForEveryItem = new HashMap<>();
        items = jdbcTemplate.query("SELECT * FROM items", itemMapper);
        Bid maxBid;
        for (Item item : items) {
            maxBid = null;
            bids = getItemBids(item.getItemId());
            for (Bid bid : bids) {
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
        users = jdbcTemplate.query("SELECT * FROM users", userMapper);
        for (User existsUser : users) {
            if (existsUser.getUserId().equals(user.getUserId())) {
                return false;
            }
        }
        updatedRowsCount = jdbcTemplate.update("INSERT INTO users VALUES (?,?,?,?,?)",
                user.getUserId(),
                user.getBillingAddress(),
                user.getFullName(),
                user.getLogin(),
                user.getPassword()
        );
        return updatedRowsCount != 0;
    }

    @Override
    public boolean createItem(Item item) {
        items = jdbcTemplate.query("SELECT * FROM items", itemMapper);
        for (Item existsItem : items) {
            if (existsItem.getItemId().equals(item.getItemId())) {
                return false;
            }
        }
        updatedRowsCount = jdbcTemplate.update("INSERT INTO items VALUES (?,?,?,?,?,?,?,?,?)",
                item.getItemId(),
                item.getBidIncrement(),
                item.getBuyItNow(),
                item.getDescription(),
                item.getStartDate(),
                item.getStartPrice(),
                item.getStopDate(),
                item.getTitle(),
                item.getUserId()
        );
        return updatedRowsCount != 0;
    }

    @Override
    public boolean createBid(Bid bid) {
        bids = jdbcTemplate.query("SELECT * FROM bids", bidMapper);
        for (Bid existsBid : bids) {
            if (existsBid.getBidId().equals(bid.getBidId())) {
                return false;
            }
        }
        updatedRowsCount = jdbcTemplate.update("INSERT INTO bids VALUES (?,?,?,?,?)",
                bid.getBidId(),
                bid.getBidDate(),
                bid.getBidValue(),
                bid.getItemId(),
                bid.getUserId()
        );
        return updatedRowsCount != 0;
    }

    @Override
    public boolean deleteUserBids(long id) {
        updatedRowsCount = jdbcTemplate.update("DELETE FROM bids WHERE user_id = ?", id);
        return updatedRowsCount != 0;
    }

    @Override
    public boolean doubleItemsStartPrice(long id) {
        updatedRowsCount = jdbcTemplate.update(
                "UPDATE items SET start_price = start_price * 2 WHERE user_id = ?", id
        );
        return updatedRowsCount != 0;
    }
}
