<!--  -->
<template>
  <el-tree
    :data="menus"
    :props="defaultProps"
    :expand-on-click-node="false"
    node-key="catId"
    ref="menuTree"
    @node-click="nodeclick"
  >
  </el-tree>
</template>

<script>
export default {
  components: {},
  props: {},
  data() {
    return {
      menus: [],
      expandedKey: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      PubSub
    };
  },
  computed: {},
  watch: {},
  methods: {
    getMenus() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get",
      }).then(({ data }) => {
        this.menus = data.data;
      });
    },
    nodeclick(data, node, component) {
      console.log("子组件的节点被点击", data, node, component);
      this.$emit("tree-node-click", data, node, component);
    },
  },
  created() {
    this.getMenus();
  },
  mounted() {},
  beforeCreate() {},
  beforeMount() {},
  beforeUpdate() {},
  update() {},
  beforeDestroy() {},
  destroyed() {},
  activated() {}, // for keep-alive cache function
};
</script>
<style scoped>
</style>