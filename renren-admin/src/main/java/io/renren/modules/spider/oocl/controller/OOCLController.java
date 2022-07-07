package io.renren.modules.spider.oocl.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.utils.ConvertUtils;
import io.renren.common.utils.Result;
import io.renren.modules.spider.oocl.vo.AccountVO;
import io.renren.modules.spider.menu.dao.AccountDao;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.utils.HttpUtils;
import io.renren.modules.spider.utils.LoginUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/7/4 14:14
 */
@RequestMapping("/oocl")
@RestController
public class OOCLController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountDao accountDao;

    @PostMapping("port")
    @RequiresPermissions("sys:schedule:all")
    public Result getPort(@RequestBody Map<String, Object> params){
        String url = "https://freightsmart.oocl.com/api/common/city/autoCompleteByFullName";
        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, String> headersMap = new HashMap<>();
        String cityFullName = params.get("cityFullName").toString();
        paramsMap.put("cityFullName", cityFullName);
        headersMap.put("Cookie",params.get("cookieValue").toString());
        headersMap.put("X-Auth-Token",params.get("tokenValue").toString());
        String result = HttpUtils.sendGet(url, headersMap, paramsMap);
        List<Map<String, Object>> resultList = JSONObject.parseObject(result, new TypeReference<List<Map<String, Object>>>() {
        });
      return new Result().ok(resultList);
    }

    @PostMapping("login")
    @RequiresPermissions("sys:schedule:all")
    public Result login(@RequestBody Map<String, Object> params){
        //1前端发送账号名 从数据库搜索然后获取密码和ip进行登录
        boolean isProxy = false;
        String username = params.get("username").toString();
        QueryWrapper<Account> qw = new QueryWrapper<>();
        qw.lambda().eq(Account::getUser,username);
        Account account = accountDao.selectOne(qw);
        Map<String, Object> accountMap = new HashMap<>();
        accountMap.put("username",account.getUser());
        accountMap.put("password",account.getPassword());
        String agentIp = account.getAgentIp();
        if (!agentIp.equals("")){
            isProxy = true;
            accountMap.put("ip",agentIp);
        }
        Map<String, Object> loginMap = LoginUtils.Login(accountMap,isProxy);
        if(loginMap != null){
            return new Result().ok(loginMap);
        }else {
            return new Result().error("登陆失败");
        }
    }

    @GetMapping("account")
    @RequiresPermissions("sys:schedule:all")
    public Result getAccount(){
        List<Account> list = accountService.getList();
        List<AccountVO> vo = ConvertUtils.sourceToTarget(list, AccountVO.class);
        return new Result<>().ok(vo);
    }


}
