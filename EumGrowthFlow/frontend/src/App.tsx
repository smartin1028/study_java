//
// App.tsx — EumGrowthFlow 메인 애플리케이션 컴포넌트
//
// Java 비교 관점에서 본 React 핵심 개념:
// ┌──────────────────────┬─────────────────────────────────────────────────┐
// │ React                │ Java                                            │
// ├──────────────────────┼─────────────────────────────────────────────────┤
// │ Component (함수)      │ Class (UI 빌더 인스턴스)                        │
// │ Props (매개변수)       │ 생성자 파라미터 / Setter 메서드 인자              │
// │ useState()            │ private field + getter/setter                   │
// │ useCallback()         │ 캐싱된 메서드 참조 (Method Reference)            │
// │ useEffect()           │ @PostConstruct + @PreDestroy 콜백               │
// │ JSX (<div>...</div>)  │ Swing 의 JPanel.add(new JButton("클릭")) 유사   │
// │ {cond && <Comp/>}     │ if (cond) { renderComp(); }                     │
// │ arr.map(x => <li/>)   │ stream().map().collect(Collectors.toList())     │
// │ ReactFlowProvider     │ Spring DI 컨테이너 (Context 제공)               │
// │ useReactFlow()        │ @Autowired / context.getBean()                  │
// │ export default        │ public class (파일 하나 = 주요 클래스 하나)     │
// └──────────────────────┴─────────────────────────────────────────────────┘
//

import { useCallback, useState } from 'react';
import ReactFlow, {
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
  BackgroundVariant,
  useReactFlow,
  ReactFlowProvider,
} from 'reactflow';
import 'reactflow/dist/style.css';

import AIAgentNode from './components/nodes/AIAgentNode';
import Sidebar from './components/Sidebar';
import NodeConfigPanel from './components/NodeConfigPanel';
import ExecutionPanel from './components/ExecutionPanel';
import ChatInterface from './components/ChatInterface';
import PasswordModal from './components/PasswordModal';
import BackendTokenModal from './components/BackendTokenModal';
import { workflowExecutor } from './services/workflowExecutor';
import {
  downloadEncryptedWorkflow,
  loadEncryptedWorkflow,
} from './utils/encryption';
import {
  setBearerToken,
  hasBearerToken,
  clearBearerToken,
} from './services/llmClient';
import './App.css';

//
// ReactFlow 용 커스텀 노드 타입 등록
// Java 비교: Map<String, Class<? extends JComponent>> nodeRenderers = new HashMap<>();
//           nodeRenderers.put("aiAgent", AIAgentNodeRenderer.class);
// ReactFlow 는 이 매핑을 보고 type="aiAgent" 인 노드를 AIAgentNode 컴포넌트로 그린다.
//
const nodeTypes = {
  aiAgent: AIAgentNode,
};

//
// 초기 캔버스 상태
// Java: List<Node> initialNodes = new ArrayList<>(Arrays.asList(new Node(...)));
//
const initialNodes = [
  {
    id: '1',
    type: 'aiAgent',
    position: { x: 250, y: 100 },
    data: {
      label: 'Start Node',
      agentType: 'input',
      description: '워크플로우 시작점',
    },
  },
];

const initialEdges: any[] = [];

//
// FlowCanvasProps 인터페이스
// Java 비교: FlowCanvas 클래스의 생성자 파라미터 시그니처를 정의한 것.
//           Java: public FlowCanvas(List<Node> nodes, List<Edge> edges, ...) { }
//           React 에서는 Props 인터페이스로 컴포넌트 간 데이터 흐름을 명시한다.
//
interface FlowCanvasProps {
  nodes: any[];
  edges: any[];
  onNodesChange: any;
  onEdgesChange: any;
  onConnect: any;
  onNodeClick: any;
  onPaneClick: any;
  setNodes: any;
  setEdges: any;
}

//
// FlowCanvas 컴포넌트 — ReactFlow 캔버스 + DnD + 키보드 삭제 처리
//
// Java 비교: JPanel 을 상속받은 내부 클래스.
//           FlowCanvas panel = new FlowCanvas(nodes, edges, ...);
//           panel.setDropTarget(new DropTarget() { ... });
//           panel.addKeyListener(new KeyAdapter() { ... });
//
// React 에서는 "컴포넌트 합성(Composition)" 으로 기능을 조립한다.
// 부모(App)가 자식(FlowCanvas)에게 Props 로 데이터와 콜백을 주입한다.
// 이는 Java 의 @Autowired / Setter DI 와 유사한 패턴이다.
//
function FlowCanvas({
  nodes,
  edges,
  onNodesChange,
  onEdgesChange,
  onConnect,
  onNodeClick,
  onPaneClick,
  setNodes,
  setEdges,
}: FlowCanvasProps) {
  //
  // useReactFlow(): ReactFlowProvider 컨텍스트에서 ReactFlow 인스턴스를 꺼낸다.
  // Java 비교: @Autowired private ReactFlowInstance reactFlowInstance;
  //           Spring DI 컨테이너에서 Bean 을 주입받는 것과 동일한 개념.
  //           반드시 <ReactFlowProvider> 내부의 자식 컴포넌트에서만 호출 가능.
  //
  const reactFlowInstance = useReactFlow();

  //
  // onDragOver: 브라우저 기본 드래그 동작을 막고 드롭을 허용한다.
  // Java: DropTargetAdapter.dropTargetDrag() 오버라이드에 해당.
  //
  const onDragOver = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  //
  // onDrop: 사이드바에서 드래그한 노드 타입 데이터를 읽어 새 노드를 생성한다.
  //
  // useCallback(fn, deps): 의존성 배열이 변경될 때만 함수를 재생성한다.
  // Java 비교: 컴파일러가 메서드 참조(String::length)를 매번 새 객체로 만들지 않고
  //           캐싱하는 것과 유사. 단, React 에서는 수동으로 deps 를 지정해야 함.
  //
  const onDrop = useCallback(
    (event: React.DragEvent) => {
      event.preventDefault();

      // HTML5 Drag and Drop API 로 사이드바에서 전송된 데이터를 읽는다.
      // Java: Transferable transferable = dropEvent.getTransferable();
      //       String type = transferable.getData("application/reactflow");
      const type = event.dataTransfer.getData('application/reactflow');
      const label = event.dataTransfer.getData('label');
      const agentType = event.dataTransfer.getData('agentType');
      const description = event.dataTransfer.getData('description');
      const icon = event.dataTransfer.getData('icon');

      //
      // screenToFlowPosition(): 브라우저 뷰포트 좌표 → ReactFlow 캔버스 내부 좌표 변환
      // Java 비교: Graphics2D 의 AffineTransform 으로 좌표계 변환하는 것.
      //           줌(zoom)과 팬(pan)이 적용된 정확한 위치를 계산해준다.
      //
      const position = reactFlowInstance.screenToFlowPosition({
        x: event.clientX,
        y: event.clientY,
      });

      const newNode = {
        id: `${Date.now()}`,     // timestamp 를 유니크 ID 로 사용
        type,
        position,
        data: { label, agentType, description, icon },
      };

      //
      // setNodes(prev => [...prev, newNode])
      // Java: List<Node> newList = new ArrayList<>(prevList);
      //       newList.add(newNode); setNodes(newList);
      //
      // React 에서는 "불변성(immutability)" 이 핵심이다.
      // 기존 배열을 직접 수정(push)하지 않고, 새 배열을 만들어 교체해야
      // React 가 변경을 감지하고 화면을 다시 그린다.
      //
      setNodes((nds: any[]) => nds.concat(newNode));
    },
    [setNodes, reactFlowInstance],
  );

  //
  // onKeyDown: Delete / Backspace 키로 선택된 노드/엣지를 삭제한다.
  // Java 비교: KeyListener.keyPressed(KeyEvent e) { if (e.getKeyCode() == VK_DELETE) ... }
  //
  const onKeyDown = useCallback(
    (event: React.KeyboardEvent) => {
      if (event.key === 'Delete' || event.key === 'Backspace') {
        // 선택된 요소들의 ID 를 수집
        // Java: List<String> selectedIds = nodes.stream()
        //          .filter(Node::isSelected).map(Node::getId).toList();
        const selectedNodeIds = nodes
          .filter((n: any) => n.selected)
          .map((n: any) => n.id);
        const selectedEdgeIds = edges
          .filter((e: any) => e.selected)
          .map((e: any) => e.id);

        if (selectedNodeIds.length > 0) {
          // 선택 안 된 노드만 남긴다 (불변성 유지)
          setNodes((nds: any[]) =>
            nds.filter((n: any) => !n.selected),
          );
          // 삭제된 노드에 연결된 엣지도 함께 정리
          setEdges((eds: any[]) =>
            eds.filter(
              (e: any) =>
                !selectedNodeIds.includes(e.source) &&
                !selectedNodeIds.includes(e.target),
            ),
          );
        }
        if (selectedEdgeIds.length > 0) {
          setEdges((eds: any[]) =>
            eds.filter((e: any) => !e.selected),
          );
        }
      }
    },
    [nodes, edges, setNodes, setEdges],
  );

  //
  // JSX 반환: React 에서 HTML-like 구문으로 UI 구조를 선언한다.
  // Java 비교: JPanel panel = new JPanel(); panel.setLayout(...);
  //           panel.add(new MiniMap()); panel.add(new Controls());
  //           panel.setBorder(...);
  //
  // React 의 선언적 UI vs Java Swing 의 명령적 UI:
  //   - React: "나는 이렇게 생긴 UI 를 원한다" (What)
  //   - Swing: "이 컴포넌트를 생성하고, 여기에 붙이고, 크기를 설정하고..." (How)
  //
  return (
    <div style={{ width: '100%', height: '100%' }} onKeyDown={onKeyDown} tabIndex={0}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        onDrop={onDrop}
        onDragOver={onDragOver}
        onNodeClick={onNodeClick}
        onPaneClick={onPaneClick}
        nodeTypes={nodeTypes}
        fitView
      >
        <Controls />
        <MiniMap />
        <Background variant={BackgroundVariant.Dots} gap={12} size={1} />
      </ReactFlow>
    </div>
  );
}

//
// App 컴포넌트 — 최상위 애플리케이션 루트
//
// Java 비교: JFrame 을 상속한 MainFrame 클래스.
//           모든 상태(state)는 App 이 소유하고, 자식 컴포넌트에 Props 로 전달한다.
//           이 패턴을 "상태 끌어올리기(Lifting State Up)" 라고 부르며,
//           Java 의 "중앙 집중식 모델(Domain Model)" 또는 "Mediator 패턴"과 유사하다.
//
function App() {
  //
  // ── 상태 선언 (State) ──────────────────────────────────────────────────
  //
  // useState(initialValue) → [현재값, setter함수] 를 반환한다. (배열 구조분해)
  // Java 비교:
  //   private List<Node> nodes = new ArrayList<>();
  //   public void setNodes(List<Node> nodes) { this.nodes = nodes; repaint(); }
  //
  // 결정적 차이: React 의 setter 호출은 "리렌더링(repaint)" 을 예약한다.
  // Java Swing 에서는 직접 repaint() 를 호출하지만, React 는 상태 변경을
  // 감지해 가상 DOM diff → 실제 DOM 업데이트를 자동으로 수행한다.
  //
  // useNodesState / useEdgesState 는 ReactFlow 전용 커스텀 훅.
  // Java: @Bean 으로 등록된 특수 Service 클래스.
  //
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);

  // useState<any>(null) → 초기값 null, 제네릭 타입 any
  // Java: private Object selectedNode = null;
  const [selectedNode, setSelectedNode] = useState<any>(null);
  const [executionResults, setExecutionResults] = useState<any[]>([]);
  const [isRunning, setIsRunning] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [showChat, setShowChat] = useState(false);
  const [chatConfig, setChatConfig] = useState<any>(null);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordMode, setPasswordMode] = useState<'save' | 'load'>('save');
  const [pendingFile, setPendingFile] = useState<File | null>(null);
  const [showTokenModal, setShowTokenModal] = useState(false);

  //
  // ── 이벤트 핸들러 (useCallback 으로 감싼 메서드들) ─────────────────────
  //
  // useCallback(fn, [deps]): 컴포넌트가 리렌더링되어도 의존성 배열의 값이
  //   바뀌지 않으면 동일한 함수 참조를 유지한다.
  // Java 비교: 불필요한 new Runnable() 생성을 막기 위해 람다를 필드에 캐싱하는 것.
  //   ex) private final Runnable onSave = () -> { ... };
  //

  //
  // 노드 간 연결(Edge) 생성 핸들러
  // addEdge(params, edges): 기존 edges 에 새 연결을 추가한 새 배열 반환 (불변)
  // Java: edges.add(new Edge(params.getSource(), params.getTarget()));
  //
  const onConnect = useCallback(
    (params: any) => setEdges((eds) => addEdge(params, eds)),
    [setEdges],
  );

  //
  // 노드 클릭 → 우측 설정 패널 열기
  //
  const onNodeClick = useCallback((_event: any, node: any) => {
    setSelectedNode(node);
  }, []);

  //
  // 빈 캔버스 클릭 → 선택 해제
  //
  const onPaneClick = useCallback(() => {
    setSelectedNode(null);
  }, []);

  //
  // 노드 설정 저장
  // map() + spread (...): 기존 노드 배열의 특정 요소만 업데이트한 새 배열 생성
  // Java: nodes.stream().map(n -> n.getId().equals(id) ? updatedNode : n).toList();
  //
  const handleSaveNodeConfig = useCallback(
    (config: any) => {
      setNodes((nds) =>
        nds.map((node) => {
          if (node.id === config.id) {
            return {
              ...node,                    // 기존 속성 복사 (Java: BeanUtils.copyProperties)
              data: {
                ...node.data,
                label: config.label,
                prompt: config.prompt,
                model: config.model,
              },
            };
          }
          return node;
        }),
      );
    },
    [setNodes],
  );

  //
  // localStorage 에 현재 워크플로우 저장
  // Java: Preferences.userNodeForPackage(App.class).put("workflow", json);
  //
  const onSave = useCallback(() => {
    const flow = { nodes, edges };
    localStorage.setItem('workflow', JSON.stringify(flow));
    alert('워크플로우가 저장되었습니다!');
  }, [nodes, edges]);

  const onLoad = useCallback(() => {
    const flowData = localStorage.getItem('workflow');
    if (flowData) {
      const flow = JSON.parse(flowData);
      setNodes(flow.nodes || []);
      setEdges(flow.edges || []);
      alert('워크플로우가 로드되었습니다!');
    } else {
      alert('저장된 워크플로우가 없습니다.');
    }
  }, [setNodes, setEdges]);

  const onClear = useCallback(() => {
    setNodes([]);
    setEdges([]);
  }, [setNodes, setEdges]);

  //
  // 워크플로우 실행 (비동기)
  // Java: CompletableFuture.supplyAsync(() -> executor.execute(nodes, edges))
  //          .thenAccept(results -> { ... });
  //
  const onExecute = useCallback(async () => {
    setIsRunning(true);
    setShowResults(true);
    setExecutionResults([]);

    try {
      const results = await workflowExecutor.executeWorkflow(nodes, edges);
      setExecutionResults(results);
    } catch (error: any) {
      setExecutionResults([
        {
          nodeId: 'error',
          success: false,
          error: error.message,
        },
      ]);
    } finally {
      setIsRunning(false);
    }
  }, [nodes, edges]);

  const handleOpenChat = useCallback(() => {
    if (selectedNode) {
      setChatConfig({
        model: selectedNode.data.model,
      });
      setShowChat(true);
      setSelectedNode(null);
    }
  }, [selectedNode]);

  //
  // 노드 삭제 (연결된 엣지도 함께 제거)
  // Java: nodes.removeIf(n -> n.getId().equals(nodeId));
  //       edges.removeIf(e -> e.getSource().equals(nodeId) || e.getTarget().equals(nodeId));
  //
  const handleDeleteNode = useCallback(
    (nodeId: string) => {
      setNodes((nds) => nds.filter((n) => n.id !== nodeId));
      setEdges((eds) =>
        eds.filter((e) => e.source !== nodeId && e.target !== nodeId),
      );
      setSelectedNode(null);
    },
    [setNodes, setEdges],
  );

  // 암호화 저장 모달 열기
  const handleEncryptedSave = useCallback(() => {
    setPasswordMode('save');
    setShowPasswordModal(true);
  }, []);

  // 비밀번호 확인 처리
  const handlePasswordConfirm = useCallback(
    (password: string) => {
      if (passwordMode === 'save') {
        try {
          downloadEncryptedWorkflow(nodes, edges, password);
          alert('🔒 암호화된 워크플로우가 다운로드되었습니다!');
        } catch (error: any) {
          alert(`❌ 저장 실패: ${error.message}`);
        }
      } else if (passwordMode === 'load' && pendingFile) {
        loadEncryptedWorkflow(pendingFile, password)
          .then(({ nodes: loadedNodes, edges: loadedEdges }) => {
            setNodes(loadedNodes);
            setEdges(loadedEdges);
            alert('✅ 암호화된 워크플로우가 로드되었습니다!');
          })
          .catch((error: any) => {
            alert(`❌ 불러오기 실패: ${error.message}`);
          })
          .finally(() => {
            setPendingFile(null);
          });
      }
      setShowPasswordModal(false);
    },
    [passwordMode, nodes, edges, pendingFile, setNodes, setEdges],
  );

  // 암호화 파일 불러오기 → 숨겨진 <input type="file"> 생성 후 클릭
  const handleEncryptedLoad = useCallback(() => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.eum';
    input.onchange = (e: any) => {
      const file = e.target.files[0];
      if (file) {
        setPendingFile(file);
        setPasswordMode('load');
        setShowPasswordModal(true);
      }
    };
    input.click();
  }, []);

  // 백엔드 API 토큰 관리 핸들러
  const handleTokenConfirm = useCallback((token: string) => {
    setBearerToken(token);
    setShowTokenModal(false);
  }, []);

  const handleTokenClear = useCallback(() => {
    clearBearerToken();
    setShowTokenModal(false);
  }, []);

  //
  // ── JSX 렌더링 ─────────────────────────────────────────────────────────
  //
  // 조건부 렌더링: {condition && <Component/>}
  // Java: if (condition) { add(component); }
  //
  // React 의 "선언적 렌더링" 핵심:
  //   - 상태(state)가 변경되면 JSX 전체를 다시 평가한다.
  //   - 조건부 블록은 상태에 따라 자연스럽게 보이거나/숨겨진다.
  //   - Java Swing: component.setVisible(condition); → 명령형
  //   - React: {condition && <Component/>} → 선언형
  //
  return (
    <div style={{ width: '100%', height: '100vh', display: 'flex' }}>
      <Sidebar />
      <div style={{ flexGrow: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        {/* 툴바 */}
        <div style={toolbarStyle}>
          <button onClick={onExecute} style={executeButtonStyle} disabled={isRunning}>
            {isRunning ? '⏳ 실행 중...' : '▶️ 실행'}
          </button>
          <button onClick={onSave} style={buttonStyle}>저장</button>
          <button onClick={onLoad} style={buttonStyle}>불러오기</button>
          <button onClick={handleEncryptedSave} style={encryptedButtonStyle}>
            🔒 암호화 저장
          </button>
          <button onClick={handleEncryptedLoad} style={encryptedButtonStyle}>
            🔓 암호화 불러오기
          </button>
          <button onClick={onClear} style={buttonStyle}>초기화</button>
          <button
            onClick={() => setShowTokenModal(true)}
            style={hasBearerToken() ? tokenActiveButtonStyle : tokenButtonStyle}
          >
            {hasBearerToken() ? '🔑 설정됨' : '🔑 API 토큰'}
          </button>
        </div>

        {/* ReactFlow 캔버스 영역 */}
        <div style={{ flexGrow: 1, position: 'relative', minHeight: 0 }}>
          {/*
            ReactFlowProvider 는 ReactFlow 의 상태(zustand store)를 자식 전체에 제공한다.
            Java: Spring ApplicationContext — BeanFactory 역할.
                  Provider 아래의 모든 컴포넌트는 useReactFlow() 로 "의존성 주입" 받을 수 있다.
          */}
          <ReactFlowProvider>
            <FlowCanvas
              nodes={nodes}
              edges={edges}
              onNodesChange={onNodesChange}
              onEdgesChange={onEdgesChange}
              onConnect={onConnect}
              onNodeClick={onNodeClick}
              onPaneClick={onPaneClick}
              setNodes={setNodes}
              setEdges={setEdges}
            />
          </ReactFlowProvider>
        </div>
      </div>

      {/* 노드 설정 패널 — 선택된 노드가 있고 채팅이 열려있지 않을 때만 표시 */}
      {selectedNode && !showChat && (
        <NodeConfigPanel
          selectedNode={{ id: selectedNode.id, ...selectedNode.data }}
          onClose={() => setSelectedNode(null)}
          onSave={handleSaveNodeConfig}
          onDelete={() => handleDeleteNode(selectedNode.id)}
          onOpenChat={
            selectedNode.data.agentType === 'llm'
              ? handleOpenChat
              : undefined
          }
        />
      )}

      {/* LLM 채팅 인터페이스 */}
      {showChat && chatConfig && (
        <ChatInterface
          nodeConfig={chatConfig}
          onClose={() => setShowChat(false)}
        />
      )}

      {/* 실행 결과 패널 */}
      {showResults && (
        <ExecutionPanel
          results={executionResults}
          isRunning={isRunning}
          onClose={() => setShowResults(false)}
        />
      )}

      {/* 암호화 비밀번호 모달 */}
      <PasswordModal
        isOpen={showPasswordModal}
        mode={passwordMode}
        onConfirm={handlePasswordConfirm}
        onCancel={() => setShowPasswordModal(false)}
      />

      {/* 백엔드 API 토큰 설정 모달 */}
      <BackendTokenModal
        isOpen={showTokenModal}
        onConfirm={handleTokenConfirm}
        onCancel={() => setShowTokenModal(false)}
        onClear={handleTokenClear}
        hasExistingToken={hasBearerToken()}
      />
    </div>
  );
}

//
// ── 스타일 상수 (CSS-in-JS) ──────────────────────────────────────────────
//
// Java 비교: Swing 의 LookAndFeel 또는 CSS Stylesheet 상수를 별도 클래스로 분리.
//           ex) public static final Color BUTTON_BG = new Color(0x1a, 0x19, 0x2b);
// React 에서는 인라인 스타일 객체나 CSS Modules, styled-components 등을 사용한다.
//
const toolbarStyle: React.CSSProperties = {
  display: 'flex',
  flexWrap: 'wrap',
  justifyContent: 'flex-end',
  gap: '10px',
  padding: '10px',
  zIndex: 4,
};

const executeButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#10b981',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const buttonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#1a192b',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
};

const encryptedButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#6366f1',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const tokenButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#f59e0b',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

// 스프레드 연산자(...)로 기존 스타일을 복사하고 일부 속성만 재정의
// Java: TokenActiveButtonStyle extends TokenButtonStyle { backgroundColor = "#059669"; }
const tokenActiveButtonStyle: React.CSSProperties = {
  ...tokenButtonStyle,
  backgroundColor: '#059669',
};

//
// export default → 이 파일의 공개 API 는 App 컴포넌트 하나다.
// Java: public class App extends JFrame { ... }
//
export default App;
