package ren.hankai.appmarket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ren.hankai.appmarket.api.payload.PaginatedList;
import ren.hankai.appmarket.config.Route;
import ren.hankai.appmarket.config.WebConfig;
import ren.hankai.appmarket.persist.model.UserGroupBean;
import ren.hankai.appmarket.persist.util.PageUtil;
import ren.hankai.appmarket.service.GroupService;

import javax.validation.Valid;

/**
 * 用户组控制器
 *
 * @author hankai
 * @version 1.0
 * @since Mar 28, 2016 2:22:51 PM
 */
@Controller
public class GroupController {

  private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
  @Autowired
  private GroupService groupService;
  @Autowired
  private MessageSource messageSource;

  @RequestMapping(Route.BG_USER_GROUPS)
  public ModelAndView getGroups() {
    return new ModelAndView("admin/groups");
  }

  @RequestMapping(
      value = Route.BG_USER_GROUPS_JSON,
      produces = {"application/json; charset=utf-8"})
  @ResponseBody
  public PaginatedList getGroupsJson(
      @RequestParam(
          value = "search",
          required = false) String search,
      @RequestParam(
          value = "order",
          required = false) String order,
      @RequestParam(
          value = "sort",
          required = false) String sort,
      @RequestParam("limit") int limit,
      @RequestParam("offset") int offset) {
    PaginatedList response = null;
    try {
      final boolean asc = "asc".equalsIgnoreCase(order);
      final Pageable pageable = PageUtil.pageWithOffsetAndCount(offset, limit, sort, asc);
      final Page<UserGroupBean> results = groupService.searchGroups(search, pageable);
      response = new PaginatedList();
      response.setTotal((int) results.getTotalElements());
      response.setRows(results.getContent());
    } catch (final Exception e) {
      logger.error("Failed to get group list.", e);
    } catch (final Error e) {
      logger.error("Failed to get group list.", e);
    }
    return response;
  }

  @GetMapping(Route.BG_ADD_GROUP)
  public ModelAndView addGroupForm() {
    final ModelAndView mav = new ModelAndView("admin/add_group");
    final UserGroupBean group = new UserGroupBean();
    group.setEnabled(true);
    mav.addObject("group", group);
    return mav;
  }

  @PostMapping(Route.BG_ADD_GROUP)
  public ModelAndView addGroup(@ModelAttribute("group") @Valid UserGroupBean group,
      BindingResult br) {
    final ModelAndView mav = new ModelAndView("admin/add_group");
    final UserGroupBean duplicate = groupService.getGroupByName(group.getName());
    if ((duplicate != null) && !duplicate.getId().equals(group.getId())) {
      br.rejectValue("name", "Duplicate.group.name");
    }
    if (br.hasErrors()) {
      mav.addObject("group", group);
    } else {
      try {
        groupService.saveGroup(group);
        mav.addObject(WebConfig.WEB_PAGE_MESSAGE,
            messageSource.getMessage("operation.success", null, null));
        mav.addObject("group", new UserGroupBean());
      } catch (final Exception e) {
        mav.addObject("group", group);
        mav.addObject(WebConfig.WEB_PAGE_ERROR,
            messageSource.getMessage("operation.fail", null, null));
      }
    }
    return mav;
  }

  @GetMapping(Route.BG_EDIT_GROUP)
  public ModelAndView editGroupForm(@PathVariable("group_id") Integer groupId) {
    final ModelAndView mav = new ModelAndView("admin/edit_group");
    final UserGroupBean group = groupService.getGroupById(groupId);
    if (group != null) {
      mav.addObject("group", group);
    } else {
      mav.setViewName("redirect:/404.html");
    }
    return mav;
  }

  @PostMapping(Route.BG_EDIT_GROUP)
  public ModelAndView editGroup(@PathVariable("group_id") Integer groupId,
      @ModelAttribute("group") @Valid UserGroupBean group, BindingResult br) {
    final ModelAndView mav = new ModelAndView("admin/edit_group");
    if (!br.hasErrors()) {
      final UserGroupBean duplicate = groupService.getGroupByName(group.getName());
      if ((duplicate != null) && !duplicate.getId().equals(groupId)) {
        br.rejectValue("name", "Duplicate.group.name");
      }
    }
    final UserGroupBean existGroup = groupService.getGroupById(groupId);
    if (existGroup == null) {
      mav.setViewName("redirect:/404.html");
    } else {
      group.setId(groupId);
      if (!br.hasErrors()) {
        try {
          existGroup.setName(group.getName());
          existGroup.setEnabled(group.isEnabled());
          groupService.saveGroup(existGroup);
          mav.addObject(WebConfig.WEB_PAGE_MESSAGE,
              messageSource.getMessage("operation.success", null, null));
        } catch (final Exception e) {
          mav.addObject(WebConfig.WEB_PAGE_ERROR,
              messageSource.getMessage("operation.fail", null, null));
        }
      }
      mav.addObject("group", group);
    }
    return mav;
  }

  @RequestMapping(Route.BG_DELETE_GROUP)
  public ModelAndView deleteGroup(@PathVariable("group_id") Integer groupId) {
    final ModelAndView mav = new ModelAndView("redirect:" + Route.BG_USER_GROUPS);
    final UserGroupBean group = groupService.getGroupById(groupId);
    if (group == null) {
      mav.setViewName("redirect:/404.html");
    } else if (group.getUsers().size() == 0) {
      groupService.deleteGroupById(groupId);
    }
    return mav;
  }

}
