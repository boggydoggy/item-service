package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@RequestParam("itemName") String itemName,
                       @RequestParam("price") int price,
                       @RequestParam("quantity") int quantity,
                       Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);

//        model.addAttribute("item", item); // @ModelAttribute("item")이 자동으로 model에 객체를 지정한 이름으로 추가,
                                            // @ModelAttribute("item2")라고 지정했다면 model에 item 객체를 item2라는 이름으로 추가

        return "basic/item";
    }

    @PostMapping("/add")
    public String addItemV3(@ModelAttribute("item") Item item, RedirectAttributes redirectAttributes) {
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());
        redirectAttributes.addAttribute("status", true);

//        return "basic/item";  // forward로 링크가 변경될 때는 새로고침하면 POST 동작이 중복되기 때문에 redirect 필요
                                // "redirect:/basic/item" + item.getId(); 하지 않은 이유는 id에 숫자가 아닌 한글이나 공백이 들어갈 경우가 있음
                                // 이를 방지하기 위해 redirectAttributes 사용
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") long itemId, @ModelAttribute("item") Item item) {
        itemRepository.update(itemId, item);

        return "redirect:/basic/items/{itemId}";
    }

    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
